package com.furnicraft.payment.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;
}