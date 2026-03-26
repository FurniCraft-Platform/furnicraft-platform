package com.furnicraft.payment.client;

import com.furnicraft.payment.client.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "order-service", path = "/api/v1/orders")
public interface OrderClient {

    @GetMapping("/{orderId}")
    OrderResponse getOrderById(@PathVariable UUID orderId);

    @PatchMapping("/{orderId}/status")
    void updateOrderStatus(
            @PathVariable("orderId") UUID orderId,
            @RequestParam("status") String status
    );
}