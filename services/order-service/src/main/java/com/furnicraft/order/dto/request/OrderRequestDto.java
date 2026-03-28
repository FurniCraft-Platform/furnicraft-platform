package com.furnicraft.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderRequestDto {

    @NotNull(message = "Address ID is required")
    private UUID addressId;

    @Valid
    @NotEmpty(message = "Order must have at least 1 item")
    private List<OrderItemRequestDto> items;
}