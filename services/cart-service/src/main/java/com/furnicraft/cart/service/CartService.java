package com.furnicraft.cart.service;

import com.furnicraft.cart.dto.AddCartItemRequest;
import com.furnicraft.cart.dto.CartResponse;
import com.furnicraft.cart.dto.UpdateCartItemRequest;

import java.util.UUID;

public interface CartService {

    CartResponse getCartByUserId(UUID userId);

    CartResponse addItemToCart(UUID userId, AddCartItemRequest request);

    CartResponse updateCartItem(UUID userId, UUID productId, UpdateCartItemRequest request);

    CartResponse removeCartItem(UUID userId, UUID productId);

    void clearCart(UUID userId);
}