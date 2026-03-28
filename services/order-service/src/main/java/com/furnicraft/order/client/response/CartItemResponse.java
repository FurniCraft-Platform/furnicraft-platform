package com.furnicraft.order.client.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CartItemResponse {

    private UUID productId;
    private Integer quantity;
}