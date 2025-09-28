package com.example.quizizz.model.dto.profile;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PublicProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private java.time.LocalDate dob;
    private String avatarURL;
    private LocalDateTime createdAt;
}