package com.example.quizizz.model.dto.profile;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dob;
}
