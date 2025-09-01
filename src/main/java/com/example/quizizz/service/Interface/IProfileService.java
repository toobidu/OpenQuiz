package com.example.quizizz.service.Interface;

import org.springframework.web.multipart.MultipartFile;

import com.example.quizizz.model.dto.profile.UpdateAvatarResponse;
import com.example.quizizz.model.dto.profile.UpdateProfileRequest;
import com.example.quizizz.model.dto.profile.UpdateProfileResponse;

public interface IProfileService {
    UpdateProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    UpdateAvatarResponse updateAvatar(Long userId, MultipartFile file) throws Exception;
    UpdateProfileResponse getProfile(Long userId);
}
