package com.example.quizizz.service.Implement;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.quizizz.common.constants.RedisKeyPrefix;
import com.example.quizizz.mapper.ProfileMapper;
import com.example.quizizz.model.dto.profile.UpdateAvatarResponse;
import com.example.quizizz.model.dto.profile.UpdateProfileRequest;
import com.example.quizizz.model.dto.profile.UpdateProfileResponse;
import com.example.quizizz.model.entity.User;
import com.example.quizizz.repository.UserRepository;
import com.example.quizizz.service.Interface.IFileStorageService;
import com.example.quizizz.service.Interface.IProfileService;
import com.example.quizizz.service.Interface.IRedisService;

import lombok.RequiredArgsConstructor;

/**
 * Service quản lý thông tin cá nhân người dùng (profile).
 * Lấy, cập nhật profile, avatar và sử dụng cache Redis để tối ưu hiệu năng.
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImplement implements IProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final IFileStorageService fileStorageService;
    private final IRedisService redisService;

    /**
     * Lấy thông tin profile của người dùng (ưu tiên lấy từ cache Redis).
     * @param userId Id người dùng
     * @return Thông tin profile
     */
    @Override
    public UpdateProfileResponse getProfile(Long userId) {
        String cacheKey = RedisKeyPrefix.USER_PROFILE.format(userId);
        Object cachedProfile = redisService.getValue(cacheKey);
        
        if (cachedProfile instanceof UpdateProfileResponse) {
            return (UpdateProfileResponse) cachedProfile;
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        UpdateProfileResponse response = profileMapper.toResponse(user);
        
        // Cache kết quả trong 1 giờ
        redisService.setValue(cacheKey, response, 3600);
        return response;
    }

    /**
     * Cập nhật thông tin profile của người dùng.
     * @param userId Id người dùng
     * @param request Thông tin cập nhật
     * @return Thông tin profile sau cập nhật
     */
    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        profileMapper.updateUserFromDto(request, user);
        userRepository.save(user);
        
        UpdateProfileResponse response = profileMapper.toResponse(user);
        
        // Cập nhật cache
        String cacheKey = RedisKeyPrefix.USER_PROFILE.format(userId);
        redisService.setValue(cacheKey, response, 3600);
        
        return response;
    }

    /**
     * Cập nhật avatar cho người dùng.
     * @param userId Id người dùng
     * @param file File avatar
     * @return Thông tin avatar mới
     * @throws Exception Nếu upload lỗi
     */
    @Override
    @Transactional
    public UpdateAvatarResponse updateAvatar(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        // Upload file mới và lưu tên file vào DB
        String fileName = fileStorageService.uploadAvatar(file, userId);
        user.setAvatarURL(fileName); // Lưu tên file thay vì URL
        userRepository.save(user);
        
        // Xóa cache profile để reload lại
        String cacheKey = RedisKeyPrefix.USER_PROFILE.format(userId);
        redisService.deleteKey(cacheKey);
        
        // Tạo presigned URL mới cho response
        String presignedUrl = fileStorageService.getAvatarUrl(fileName);
        UpdateAvatarResponse response = new UpdateAvatarResponse();
        response.setAvatarURL(presignedUrl);
        return response;
    }

    /**
     * Lấy URL avatar của người dùng.
     * @param userId Id người dùng
     * @return Avatar URL
     * @throws Exception Nếu lỗi
     */
    @Override
    public String getAvatarUrl(Long userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        String avatarURL = user.getAvatarURL();
        if (avatarURL == null || avatarURL.isEmpty()) {
            return null;
        }
        
        // Tạo presigned URL mới để đảm bảo không hết hạn
        String fileName = extractFileNameFromUrl(avatarURL);
        return fileStorageService.getAvatarUrl(fileName);
    }
    
    private String extractFileNameFromUrl(String url) {
        if (url == null) return null;
        // Extract filename from MinIO URL (before query parameters)
        String[] parts = url.split("/");
        String fileNameWithParams = parts[parts.length - 1];
        return fileNameWithParams.split("\\?")[0]; // Remove query parameters
    }
}
