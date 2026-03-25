package com.furnicraft.order.controller;

import com.furnicraft.order.dto.request.OrderRequestDto;
import com.furnicraft.order.dto.response.OrderResponseDto;
import com.furnicraft.order.enums.OrderStatus;
import com.furnicraft.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderResponseDto>> getUserOrders(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, userId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}