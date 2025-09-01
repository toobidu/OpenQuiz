package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.authentication.LoginRequest;
import com.example.quizizz.model.dto.authentication.LoginResponse;
import com.example.quizizz.model.dto.authentication.RegisterRequest;
import com.example.quizizz.model.dto.authentication.RegisterResponse;
import com.example.quizizz.model.dto.authentication.ResetPasswordRequest;
import com.example.quizizz.model.dto.authentication.ResetPasswordResponse;

public interface IAuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
    LoginResponse refreshToken(String refreshToken);
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
    void logoutAllDevices(Long userId);
}