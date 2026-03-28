package com.furnicraft.order.client.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartResponse {

    private UUID id;
    private UUID userId;
    private List<CartItemResponse> items;
}