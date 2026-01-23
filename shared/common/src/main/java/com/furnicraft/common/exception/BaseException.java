package com.furnicraft.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(String message, ErrorCode errorCode) {
        super(message != null ? message : errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
}
