package com.example.quizizz.config;

import com.example.quizizz.enums.MessageCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, MessageCode.SUCCESS.getCode(), MessageCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(MessageCode messageCode, T data) {
        return new ApiResponse<>(200, messageCode.getCode(), messageCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> error(int status, MessageCode messageCode) {
        return new ApiResponse<>(status, messageCode.getCode(), messageCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(int status, MessageCode messageCode, String customMessage) {
        return new ApiResponse<>(status, messageCode.getCode(), customMessage, null);
    }
}
