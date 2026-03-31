package com.furnicraft.cart.controller;

import com.furnicraft.cart.dto.AddCartItemRequest;
import com.furnicraft.cart.dto.CartResponse;
import com.furnicraft.cart.dto.UpdateCartItemRequest;
import com.furnicraft.cart.service.CartService;
import com.furnicraft.security.auth.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(
        name = "Cart",
        description = "Shopping cart management endpoints for authenticated users. " +
                "All operations are executed in the context of the current user derived from X-Auth-* headers."
)
public class CartController {

    private final CartService cartService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Operation(
            summary = "Get current user's cart",
            description = "Returns the shopping cart of the authenticated user. " +
                    "User is resolved via security context populated from gateway headers."
    )
    @GetMapping
    @PreAuthorize("hasAuthority('CART_READ')")
    public CartResponse getMyCart() {
        return cartService.getCartByUserId(authenticatedUserProvider.getUserId());
    }

    @Operation(
            summary = "Add item to cart",
            description = "Adds a product to the authenticated user's cart. " +
                    "If the product already exists, quantity may be incremented."
    )
    @PostMapping("/items")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public CartResponse addItemToCart(
            @Parameter(description = "Cart item payload", required = true)
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItemToCart(authenticatedUserProvider.getUserId(), request);
    }

    @Operation(
            summary = "Update cart item quantity",
            description = "Updates the quantity of a specific product in the authenticated user's cart."
    )
    @PutMapping("/items/{productId}")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public CartResponse updateCartItem(
            @Parameter(description = "Product ID to update", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Updated quantity payload", required = true)
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateCartItem(authenticatedUserProvider.getUserId(), productId, request);
    }

    @Operation(
            summary = "Remove item from cart",
            description = "Removes a specific product from the authenticated user's cart."
    )
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public CartResponse removeCartItem(
            @Parameter(description = "Product ID to remove", required = true)
            @PathVariable UUID productId
    ) {
        return cartService.removeCartItem(authenticatedUserProvider.getUserId(), productId);
    }

    @Operation(
            summary = "Clear cart",
            description = "Removes all items from the authenticated user's cart."
    )
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('CART_WRITE')")
    public void clearCart() {
        cartService.clearCart(authenticatedUserProvider.getUserId());
    }

    @Hidden
    @GetMapping("/internal/{userId}")
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public CartResponse getCartByUserIdInternal(@PathVariable UUID userId) {
        return cartService.getCartByUserId(userId);
    }

    @Hidden
    @DeleteMapping("/internal/{userId}/clear")
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public void clearCartInternal(@PathVariable UUID userId) {
        cartService.clearCart(userId);
    }
}