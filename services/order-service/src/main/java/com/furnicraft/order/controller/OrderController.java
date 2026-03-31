package com.furnicraft.order.controller;

import com.furnicraft.order.dto.request.OrderRequestDto;
import com.furnicraft.order.dto.response.OrderResponseDto;
import com.furnicraft.order.enums.OrderStatus;
import com.furnicraft.order.service.OrderService;
import com.furnicraft.security.auth.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(
        name = "Order",
        description = "Order creation, checkout, listing, cancellation and status management endpoints"
)
public class OrderController {

    private final OrderService orderService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Operation(
            summary = "Create order manually",
            description = "Creates an order directly for the authenticated user. " +
                    "Useful as a fallback/manual order creation flow if checkout-from-cart is not used."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    public ResponseEntity<OrderResponseDto> createOrder(
            @Parameter(description = "Manual order creation payload", required = true)
            @Valid @RequestBody OrderRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(authenticatedUserProvider.getUserId(), request));
    }

    @Operation(
            summary = "Create order from cart",
            description = "Creates an order from the authenticated user's current cart using the selected address."
    )
    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    public ResponseEntity<OrderResponseDto> createOrderFromCart(
            @Parameter(description = "Address ID selected for checkout", required = true)
            @RequestParam UUID addressId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrderFromCart(authenticatedUserProvider.getUserId(), addressId));
    }

    @Operation(
            summary = "Get current user's order by id",
            description = "Returns a single order belonging to the authenticated user."
    )
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
                orderService.getOrderByIdForUser(orderId, authenticatedUserProvider.getUserId())
        );
    }

    @Operation(
            summary = "Get current user's orders",
            description = "Returns paginated orders of the authenticated user."
    )
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(authenticatedUserProvider.getUserId(), pageable));
    }

    @Operation(
            summary = "Get orders by user id",
            description = "Returns paginated orders for a specific user. Accessible only by ADMIN."
    )
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> getUserOrdersAsAdmin(
            @Parameter(description = "Target user ID", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Pagination and sorting parameters")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @Operation(
            summary = "Get order by id as admin",
            description = "Returns any order by id. Accessible only by ADMIN."
    )
    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> getOrderByIdAsAdmin(
            @Parameter(description = "Order ID", required = true)
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Operation(
            summary = "Cancel current user's order",
            description = "Cancels an order belonging to the authenticated user if business rules allow cancellation."
    )
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, authenticatedUserProvider.getUserId()));
    }

    @Operation(
            summary = "Update order status",
            description = "Updates status of any order. Accessible to users with ORDER_MANAGE authority."
    )
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('ORDER_MANAGE')")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @Parameter(description = "Order ID", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "New order status", required = true)
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}