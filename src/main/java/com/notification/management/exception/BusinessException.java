package com.notification.management.exception;

import lombok.Getter;

/**
 * Exception for business validation and logic errors.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
