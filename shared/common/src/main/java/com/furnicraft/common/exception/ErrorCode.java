package com.furnicraft.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token expired"),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),

    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
}
