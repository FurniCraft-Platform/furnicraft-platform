package com.furnicraft.cart.dto;

import com.furnicraft.cart.enums.CartStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CartResponse {
    private UUID id;
    private UUID userId;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private CartStatus status;
    private List<CartItemResponse> items;
}
