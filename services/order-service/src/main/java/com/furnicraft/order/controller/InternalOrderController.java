package com.furnicraft.order.controller;

import com.furnicraft.order.dto.response.OrderResponseDto;
import com.furnicraft.order.enums.OrderStatus;
import com.furnicraft.order.service.OrderService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders/internal")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public ResponseEntity<OrderResponseDto> getOrderByIdInternal(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdInternal(orderId));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public ResponseEntity<Void> updateOrderStatusInternal(
            @PathVariable UUID orderId,
            @RequestParam @NotNull OrderStatus status
    ) {
        orderService.updateOrderStatusInternal(orderId, status);
        return ResponseEntity.noContent().build();
    }
}