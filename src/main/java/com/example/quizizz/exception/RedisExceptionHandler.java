package com.example.quizizz.exception;

import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.quizizz.config.ApiResponse;
import com.example.quizizz.enums.MessageCode;

@RestControllerAdvice
public class RedisExceptionHandler {

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleRedisConnectionException(RedisConnectionFailureException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), MessageCode.REDIS_CONNECTION_ERROR, "Redis connection failed"));
    }

    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ApiResponse<Object>> handleRedisTimeoutException(QueryTimeoutException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), MessageCode.CACHE_ERROR, "Cache service timeout"));
    }
}