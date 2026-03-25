package com.furnicraft.order.dto.response;

import com.furnicraft.order.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponseDto {
    private UUID id;
    private UUID userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private List<OrderItemResponseDto> items;
}