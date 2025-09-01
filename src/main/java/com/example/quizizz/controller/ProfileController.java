package com.example.quizizz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.quizizz.model.dto.profile.UpdateAvatarResponse;
import com.example.quizizz.model.dto.profile.UpdateProfileRequest;
import com.example.quizizz.model.dto.profile.UpdateProfileResponse;
import com.example.quizizz.service.Interface.IProfileService;
import com.example.quizizz.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Quản lý thông tin cá nhân người dùng")
public class ProfileController {

    private final IProfileService profileService;

    @Operation(summary = "Lấy thông tin cá nhân", description = "Trả về thông tin cá nhân của người dùng theo userId")
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    public ResponseEntity<UpdateProfileResponse> getProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @Operation(summary = "Cập nhật thông tin cá nhân", description = "Cập nhật thông tin cá nhân của người dùng")
    @PutMapping
    @PreAuthorize("hasAuthority('user:manage_profile')")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(profileService.updateProfile(userId, request));
    }

    @Operation(summary = "Cập nhật avatar", description = "Cập nhật avatar của người dùng")
    @PostMapping("/avatar")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    public ResponseEntity<UpdateAvatarResponse> updateAvatar(
            @RequestParam("file") MultipartFile file) throws Exception {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(profileService.updateAvatar(userId, file));
    }
}
