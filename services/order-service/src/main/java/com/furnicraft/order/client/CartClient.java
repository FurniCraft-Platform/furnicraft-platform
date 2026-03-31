package com.furnicraft.order.client;

import com.furnicraft.order.client.response.CartResponse;
import com.furnicraft.order.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "cart-service",
        path = "/api/v1/carts/internal",
        configuration = InternalFeignConfig.class
)
public interface CartClient {

    @GetMapping("/{userId}")
    CartResponse getCartByUserId(@PathVariable UUID userId);

    @DeleteMapping("/{userId}/clear")
    void clearCart(@PathVariable UUID userId);
}