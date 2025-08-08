package com.example.quizizz.exception;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.quizizz.config.ApiResponse;
import com.example.quizizz.enums.MessageCode;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class DatabaseExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), MessageCode.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = "Data integrity violation";
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Duplicate entry found";
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), MessageCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageCode.INTERNAL_ERROR, "Database access error"));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLException(SQLException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), MessageCode.INTERNAL_ERROR, "Database error"));
    }
}