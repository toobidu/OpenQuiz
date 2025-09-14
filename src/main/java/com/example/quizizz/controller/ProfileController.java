package com.example.quizizz.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.quizizz.common.config.ApiResponse;
import com.example.quizizz.common.constants.MessageCode;
import com.example.quizizz.model.dto.profile.UpdateAvatarResponse;
import com.example.quizizz.model.dto.profile.UpdateProfileRequest;
import com.example.quizizz.model.dto.profile.UpdateProfileResponse;
import com.example.quizizz.service.Interface.IProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Profile", description = "APIs liên quan đến hồ sơ người dùng")
public class ProfileController {

    private final IProfileService profileService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Lấy hồ sơ người dùng", description = "Lấy thông tin hồ sơ của người dùng hiện tại")
    public ResponseEntity<ApiResponse<UpdateProfileResponse>> getProfile(Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        UpdateProfileResponse response = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Cập nhật hồ sơ người dùng", description = "Cập nhật thông tin hồ sơ của người dùng hiện tại")
    public ResponseEntity<ApiResponse<UpdateProfileResponse>> updateProfile(@RequestBody UpdateProfileRequest request, Authentication auth) {
        Long userId = Long.valueOf(auth.getName());
        UpdateProfileResponse response = profileService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Tải lên ảnh đại diện", description = "Tải lên ảnh đại diện cho người dùng hiện tại")
    public ResponseEntity<ApiResponse<UpdateAvatarResponse>> updateAvatar(
            @Parameter(description = "File ảnh đại diện", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        try {
            Long userId = Long.valueOf(auth.getName());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, MessageCode.EMPTY_FILE));
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, MessageCode.INVALID_FILE_TYPE));
            }
            
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, MessageCode.FILE_TOO_LARGE));
            }
            
            UpdateAvatarResponse response = profileService.updateAvatar(userId, file);
            return ResponseEntity.ok(ApiResponse.success(MessageCode.AVATAR_UPDATED, response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, MessageCode.INTERNAL_SERVER_ERROR, "Lỗi upload avatar: " + e.getMessage()));
        }
    }

    @GetMapping("/avatar")
    @PreAuthorize("hasAuthority('user:manage_profile')")
    @Operation(summary = "Lấy URL ảnh đại diện", description = "Lấy đường dẫn truy cập ảnh đại diện của người dùng hiện tại")
    public ResponseEntity<ApiResponse<String>> getAvatarUrl(Authentication auth) {
        try {
            Long userId = Long.valueOf(auth.getName());
            String avatarUrl = profileService.getAvatarUrl(userId);
            return ResponseEntity.ok(ApiResponse.success(MessageCode.AVATAR_URL_RETRIEVED, avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, MessageCode.INTERNAL_SERVER_ERROR, "Lỗi lấy avatar: " + e.getMessage()));
        }
    }
}
