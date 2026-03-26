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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public ResponseEntity<Page<OrderResponseDto>> getUserOrders(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, userId));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('ORDER_MANAGE')")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}