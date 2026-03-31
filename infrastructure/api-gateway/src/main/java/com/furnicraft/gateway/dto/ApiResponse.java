package com.furnicraft.gateway.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private String correlationId;
    private String timestamp;
    private Object errors;
}