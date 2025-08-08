package com.example.quizizz.exception;

import com.example.quizizz.enums.MessageCode;

public class ApiException extends RuntimeException {
    private final int status;
    private final MessageCode messageCode;

    public ApiException(int status, MessageCode messageCode) {
        super(messageCode.getDefaultMessage());
        this.status = status;
        this.messageCode = messageCode;
    }

    public ApiException(int status, MessageCode messageCode, String customMessage) {
        super(customMessage);
        this.status = status;
        this.messageCode = messageCode;
    }

    public int getStatus() {
        return status;
    }

    public MessageCode getMessageCode() {
        return messageCode;
    }
}