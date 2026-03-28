package com.furnicraft.order.controller;

import com.furnicraft.order.dto.request.OrderRequestDto;
import com.furnicraft.order.dto.response.OrderResponseDto;
import com.furnicraft.order.enums.OrderStatus;
import com.furnicraft.order.service.OrderService;
import com.furnicraft.security.jwt.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order management and checkout endpoints")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create order manually (testing/fallback)")
    @PostMapping
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @Valid @RequestBody OrderRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(currentUser.userId(), request));
    }

    @Operation(summary = "Create order from current user's cart")
    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    public ResponseEntity<OrderResponseDto> createOrderFromCart(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestParam UUID addressId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrderFromCart(currentUser.userId(), addressId));
    }

    @Operation(summary = "Get current user's order by id")
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderByIdForUser(orderId, currentUser.userId()));
    }

    @Operation(summary = "Get current user's orders")
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(currentUser.userId(), pageable));
    }

    @Operation(summary = "Get any user's orders (admin only)")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> getUserOrdersAsAdmin(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @Operation(summary = "Get any order by id (admin only)")
    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> getOrderByIdAsAdmin(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Operation(summary = "Cancel current user's order")
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, currentUser.userId()));
    }

    @Operation(summary = "Update order status (admin/manager only)")
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('ORDER_MANAGE')")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}