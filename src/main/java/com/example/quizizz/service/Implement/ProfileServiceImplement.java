package com.example.quizizz.service.Implement;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.quizizz.enums.RedisKeyPrefix;
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
    public UpdateAvatarResponse updateAvatar(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        String avatarUrl = fileStorageService.uploadAvatar(file, userId); 
        user.setAvatarURL(avatarUrl);
        userRepository.save(user);
        
        // Xóa cache profile để reload lại
        String cacheKey = RedisKeyPrefix.USER_PROFILE.format(userId);
        redisService.deleteKey(cacheKey);
        
        UpdateAvatarResponse response = new UpdateAvatarResponse();
        response.setAvatarURL(avatarUrl);
        return response;
    }
}
