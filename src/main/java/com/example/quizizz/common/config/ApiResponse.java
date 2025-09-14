package com.example.quizizz.common.config;

import com.example.quizizz.common.constants.MessageCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private String timestamp;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage(MessageCode.SUCCESS.getMessage());
        response.setData(data);
        response.setTimestamp(java.time.Instant.now().toString());
        return response;
    }

    public static <T> ApiResponse<T> success(MessageCode messageCode, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage(messageCode.getMessage());
        response.setData(data);
        response.setTimestamp(java.time.Instant.now().toString());
        return response;
    }

    public static <T> ApiResponse<T> error(int status, MessageCode messageCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status);
        response.setMessage(messageCode.getMessage());
        response.setData(null);
        response.setTimestamp(java.time.Instant.now().toString());
        return response;
    }

    public static <T> ApiResponse<T> error(int status, MessageCode messageCode, String customMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status);
        response.setMessage(customMessage);
        response.setData(null);
        response.setTimestamp(java.time.Instant.now().toString());
        return response;
    }
}
