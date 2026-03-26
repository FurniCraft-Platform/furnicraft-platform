package com.furnicraft.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists with email"),

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token expired"),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),

    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "Category already exists with this name"),

    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Product already exists with this name"),

    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Insufficient stock"),

    ORDER_CANNOT_BE_CANCELLED(HttpStatus.CONFLICT, "Order cannot be cancelled"),

    INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "Invalid status transition"),

    PAYMENT_ALREADY_COMPLETED(HttpStatus.CONFLICT, "Payment already completed for this order"),

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found"),

    PAYMENT_CANNOT_BE_REFUNDED(HttpStatus.CONFLICT, "Payment cannot be refunded in current status");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
}
