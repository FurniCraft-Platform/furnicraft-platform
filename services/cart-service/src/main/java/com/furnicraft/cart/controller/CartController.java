package com.furnicraft.cart.controller;

import com.furnicraft.cart.dto.AddCartItemRequest;
import com.furnicraft.cart.dto.CartResponse;
import com.furnicraft.cart.dto.UpdateCartItemRequest;
import com.furnicraft.cart.service.CartService;
import com.furnicraft.security.jwt.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart endpoints")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get current user's cart")
    @GetMapping
    @PreAuthorize("hasAuthority('CART_READ')")
    public CartResponse getMyCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return cartService.getCartByUserId(currentUser.userId());
    }

    @Operation(summary = "Add item to current user's cart")
    @PostMapping("/items")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public CartResponse addItemToCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItemToCart(currentUser.userId(), request);
    }

    @Operation(summary = "Update item quantity in current user's cart")
    @PutMapping("/items/{productId}")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public CartResponse updateCartItem(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateCartItem(currentUser.userId(), productId, request);
    }

    @Operation(summary = "Remove item from current user's cart")
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public CartResponse removeCartItem(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable UUID productId
    ) {
        return cartService.removeCartItem(currentUser.userId(), productId);
    }

    @Operation(summary = "Clear current user's cart")
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public void clearCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        cartService.clearCart(currentUser.userId());
    }

    @Hidden
    @GetMapping("/internal/{userId}")
    public CartResponse getCartByUserIdInternal(@PathVariable UUID userId) {
        return cartService.getCartByUserId(userId);
    }

    @Hidden
    @DeleteMapping("/internal/{userId}/clear")
    public void clearCartInternal(@PathVariable UUID userId) {
        cartService.clearCart(userId);
    }
}