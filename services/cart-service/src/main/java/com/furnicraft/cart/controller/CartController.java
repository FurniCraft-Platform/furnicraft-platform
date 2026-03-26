package com.furnicraft.cart.controller;

import com.furnicraft.cart.dto.AddCartItemRequest;
import com.furnicraft.cart.dto.CartResponse;
import com.furnicraft.cart.dto.UpdateCartItemRequest;
import com.furnicraft.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public CartResponse getCartByUserId(@PathVariable UUID userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/{userId}/items")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public CartResponse addItemToCart(
            @PathVariable UUID userId,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItemToCart(userId, request);
    }

    @PutMapping("/{userId}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public CartResponse updateCartItem(
            @PathVariable UUID userId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateCartItem(userId, itemId, request);
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public CartResponse removeCartItem(
            @PathVariable UUID userId,
            @PathVariable UUID itemId
    ) {
        return cartService.removeCartItem(userId, itemId);
    }

    @DeleteMapping("/{userId}/clear")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public CartResponse clearCart(@PathVariable UUID userId) {
        return cartService.clearCart(userId);
    }
}