package com.furnicraft.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {
    @PatchMapping("/api/v1/orders/{orderId}/status")
    void updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status
    );
}