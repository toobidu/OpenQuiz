package com.example.quizizz.exception;

import com.example.quizizz.config.ApiResponse;
import com.example.quizizz.enums.MessageCode;
import io.minio.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error(HttpStatus.PAYLOAD_TOO_LARGE.value(), MessageCode.FILE_TOO_LARGE, "File size exceeds maximum limit"));
    }

    @ExceptionHandler({BucketPolicyTooLargeException.class, ErrorResponseException.class})
    public ResponseEntity<ApiResponse<Object>> handleMinioException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageCode.UPLOAD_FAILED, "File upload failed"));
    }

    @ExceptionHandler(InvalidResponseException.class)
    public ResponseEntity<ApiResponse<Object>> handleMinioConnectionException(InvalidResponseException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), MessageCode.UPLOAD_FAILED, "Storage service unavailable"));
    }
}