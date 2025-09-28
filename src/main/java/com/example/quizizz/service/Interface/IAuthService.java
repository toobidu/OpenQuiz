package com.example.quizizz.service.Interface;

import com.example.quizizz.model.dto.authentication.ChangePasswordRequest;
import com.example.quizizz.model.dto.authentication.ChangePasswordResponse;
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
    ChangePasswordResponse changePassword(Long userId, ChangePasswordRequest request);
    void logoutAllDevices(Long userId);
}
