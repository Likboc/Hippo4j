package com.example.exception;

import com.example.entity.ErrorCode;
import lombok.Getter;

public class AbstractException extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    public AbstractException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode;
    }
}
