package com.example.quizizz.service.Implement;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quizizz.enums.MessageCode;
import com.example.quizizz.enums.RoleCode;
import com.example.quizizz.enums.SystemFlag;
import com.example.quizizz.exception.ApiException;
import com.example.quizizz.mapper.UserMapper;
import com.example.quizizz.model.dto.authentication.LoginRequest;
import com.example.quizizz.model.dto.authentication.LoginResponse;
import com.example.quizizz.model.dto.authentication.RegisterRequest;
import com.example.quizizz.model.dto.authentication.RegisterResponse;
import com.example.quizizz.model.dto.authentication.ResetPasswordRequest;
import com.example.quizizz.model.dto.authentication.ResetPasswordResponse;
import com.example.quizizz.model.entity.User;
import com.example.quizizz.model.entity.UserRole;
import com.example.quizizz.repository.PermissionRepository;
import com.example.quizizz.repository.RoleRepository;
import com.example.quizizz.repository.UserRepository;
import com.example.quizizz.repository.UserRoleRepository;
import com.example.quizizz.security.JwtUtil;
import com.example.quizizz.service.Interface.IAuthService;
import com.example.quizizz.service.Interface.IRedisService;
import com.example.quizizz.service.Interface.IEmailService;
import com.example.quizizz.util.PasswordGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImplement implements IAuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final IRedisService redisService;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final IEmailService emailService;
    private final PasswordGenerator passwordGenerator;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.USER_ALREADY_EXISTS, "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.USER_ALREADY_EXISTS, "Email already exists");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ApiException(HttpStatus.CONFLICT.value(), MessageCode.USER_ALREADY_EXISTS, "Phone number already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setSystemFlag(SystemFlag.NORMAL.getValue());
        User savedUser = userRepository.save(user);

        // Gán role PLAYER mặc định
        var playerRole = roleRepository.findByRoleName(RoleCode.PLAYER.name())
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageCode.ROLE_NOT_FOUND, "Default role PLAYER not found"));
        UserRole userRole = new UserRole();
        userRole.setUserId(savedUser.getId());
        userRole.setRoleId(playerRole.getId());
        userRoleRepository.save(userRole);

        // Lưu quyền vào Redis
        refreshUserPermissionsInRedis(savedUser.getId());

        return userMapper.toRegisterResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED.value(), MessageCode.USER_NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED.value(), MessageCode.AUTH_PASSWORD_INCORRECT, "Incorrect password");
        }

        user.setOnline(true);
        userRepository.save(user);
        redisService.setUserOnline(user.getId());

        // Làm mới quyền trong Redis mỗi lần login
        refreshUserPermissionsInRedis(user.getId());

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getTypeAccount(), "BRONZE");
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return userMapper.toLoginResponse(user, accessToken, refreshToken);
    }

    @Override
    public void logout(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.USER_NOT_FOUND));
            user.setOnline(false);
            user.setLastOnlineTime(LocalDateTime.now());
            userRepository.save(user);
            redisService.setUserOffline(userId);

            long expiration = jwtUtil.getClaimFromToken(token, claims -> claims.getExpiration()).getTime();
            redisService.addTokenToBlacklist(token, expiration);

        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST.value(), MessageCode.AUTH_INVALID_TOKEN, "Invalid token");
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new ApiException(HttpStatus.UNAUTHORIZED.value(), MessageCode.AUTH_INVALID_TOKEN, "Invalid or expired refresh token");
            }
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.USER_NOT_FOUND));

            // Làm mới quyền khi refresh token
            refreshUserPermissionsInRedis(userId);

            String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getTypeAccount(), "BRONZE");
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

            return userMapper.toLoginResponse(user, newAccessToken, newRefreshToken);

        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED.value(), MessageCode.AUTH_INVALID_TOKEN, "Invalid refresh token");
        }
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        try {
            log.info("Bắt đầu xử lý reset password cho email: {}", request.getEmail());
            
            // 1. Tìm user theo email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), 
                            MessageCode.USER_NOT_FOUND, "User not found with email: " + request.getEmail()));

            // 2. Generate password mới
            String newPassword = passwordGenerator.generateSecurePassword();
            log.info("Đã tạo password mới cho user: {}", user.getUsername());

            // 3. Hash và cập nhật password trong database
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword);
            userRepository.save(user);
            log.info("Đã cập nhật password mới trong database cho user: {}", user.getUsername());

            // 4. Logout tất cả devices của user này
            logoutAllDevices(user.getId());
            log.info("Đã logout tất cả devices cho user: {}", user.getUsername());

            // 5. Gửi email với password mới
            boolean emailSent = emailService.sendPasswordResetEmail(
                    request.getEmail(), 
                    user.getUsername(), 
                    newPassword
            );

            if (!emailSent) {
                log.error("Không thể gửi email reset password cho user: {}", user.getUsername());
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                        MessageCode.AUTH_EMAIL_SEND_FAILED, "Failed to send reset password email");
            }

            log.info("Hoàn thành reset password cho user: {}", user.getUsername());
            return new ResetPasswordResponse("Password reset successfully. Please check your email for the new password.", request.getEmail());

        } catch (ApiException e) {
            log.error("Lỗi reset password: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi reset password cho email {}: {}", request.getEmail(), e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    MessageCode.AUTH_PASSWORD_RESET_FAILED, "Password reset failed");
        }
    }

    @Override
    public void logoutAllDevices(Long userId) {
        try {
            log.info("Bắt đầu logout tất cả devices cho user ID: {}", userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND.value(), MessageCode.USER_NOT_FOUND));

            // Set user offline
            user.setOnline(false);
            user.setLastOnlineTime(LocalDateTime.now());
            userRepository.save(user);

            // Xóa user khỏi Redis online users
            redisService.setUserOffline(userId);

            // Xóa cache permissions của user (force refresh khi login lại)
            redisService.deleteUserPermissionsCache(userId);

            // Note: Trong thực tế, ta cần blacklist tất cả JWT tokens của user này
            // Nhưng vì JWT là stateless, cách đơn giản nhất là thay đổi secret key hoặc 
            // lưu thời gian logout trong database và kiểm tra khi validate token
            // Ở đây ta sẽ implement cách đơn giản bằng cách lưu thời gian logout

            log.info("Đã logout tất cả devices cho user ID: {}", userId);

        } catch (Exception e) {
            log.error("Lỗi khi logout tất cả devices cho user {}: {}", userId, e.getMessage(), e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    MessageCode.INTERNAL_ERROR, "Failed to logout all devices");
        }
    }

    /**
     * Làm mới quyền người dùng trong Redis
     */
    private void refreshUserPermissionsInRedis(Long userId) {
        try {
            // Lấy danh sách permission từ database (dạng "user:manage_profile")
            Set<String> permissionNames = permissionRepository.findPermissionsByUserId(userId)
                    .stream()
                    .map(p -> p.getPermissionName())
                    .collect(Collectors.toSet());

            log.info("Permissions from DB for user {}: {}", userId, permissionNames);

            // Chuyển từ code sang enum PermissionCode
            Set<com.example.quizizz.enums.PermissionCode> permissionCodes = permissionNames.stream()
                    .map(code -> {
                        for (com.example.quizizz.enums.PermissionCode p : com.example.quizizz.enums.PermissionCode.values()) {
                            if (p.getCode().equals(code)) return p;
                        }
                        log.warn("Invalid permission code: {}", code);
                        return null;
                    })
                    .filter(p -> p != null)
                    .collect(Collectors.toSet());

            log.info("Permission codes for user {}: {}", userId, permissionCodes);

            // Lưu vào Redis
            redisService.saveUserPermissions(userId, permissionCodes);

            log.info("Successfully saved permissions to Redis for user: {}", userId);
        } catch (Exception e) {
            log.error("Error refreshing user permissions for user {}: {}", userId, e.getMessage(), e);
        }
    }
}

