package com.example.quizizz.model.dto.profile;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UpdateProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dob;
    private String avatarURL;
    private LocalDateTime createdAt;
}
