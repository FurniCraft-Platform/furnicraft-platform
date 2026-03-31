package com.furnicraft.payment.client;

import com.furnicraft.payment.client.dto.OrderResponse;
import com.furnicraft.payment.config.InternalFeignConfig;
import com.furnicraft.payment.enums.OrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "order-service",
        path = "/api/v1/orders/internal",
        configuration = InternalFeignConfig.class
)
public interface OrderClient {

    @GetMapping("/{orderId}")
    OrderResponse getOrderById(@PathVariable UUID orderId);

    @PatchMapping("/{orderId}/status")
    void updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam("status") OrderStatus status
    );
}