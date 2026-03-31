package com.furnicraft.order.service;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.order.client.CartClient;
import com.furnicraft.order.client.ProductClient;
import com.furnicraft.order.client.UserClient;
import com.furnicraft.order.client.response.CartItemResponse;
import com.furnicraft.order.client.response.CartResponse;
import com.furnicraft.order.client.response.ProductResponse;
import com.furnicraft.order.dto.request.OrderItemRequestDto;
import com.furnicraft.order.dto.request.OrderRequestDto;
import com.furnicraft.order.dto.response.OrderResponseDto;
import com.furnicraft.order.entity.Order;
import com.furnicraft.order.entity.OrderItem;
import com.furnicraft.order.enums.OrderStatus;
import com.furnicraft.order.mapper.OrderMapper;
import com.furnicraft.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final CartClient cartClient;

    @Transactional
    public OrderResponseDto createOrder(UUID userId, OrderRequestDto request) {
        var address = userClient.getAddressById(userId, request.getAddressId());
        String fullAddress = buildFullAddress(address.getCountry(), address.getCity(), address.getStreet());

        Order order = Order.builder()
                .userId(userId)
                .shippingAddress(fullAddress)
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> preparedItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequestDto itemRequest : request.getItems()) {
            ProductResponse product = productClient.getProductById(itemRequest.getProductId());

            validateStock(product, itemRequest.getQuantity());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            preparedItems.add(buildOrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    itemRequest.getQuantity(),
                    subtotal
            ));
        }

        for (OrderItemRequestDto itemRequest : request.getItems()) {
            productClient.reduceStock(itemRequest.getProductId(), itemRequest.getQuantity());
        }

        for (OrderItem item : preparedItems) {
            order.addItem(item);
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    public OrderResponseDto createOrderFromCart(UUID userId, UUID addressId) {
        CartResponse cart = cartClient.getCartByUserId(userId);

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BaseException("Cart is empty", ErrorCode.CART_EMPTY);
        }

        var address = userClient.getAddressById(userId, addressId);
        String fullAddress = buildFullAddress(address.getCountry(), address.getCity(), address.getStreet());

        Order order = Order.builder()
                .userId(userId)
                .shippingAddress(fullAddress)
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> preparedItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemResponse cartItem : cart.getItems()) {
            ProductResponse product = productClient.getProductById(cartItem.getProductId());

            validateStock(product, cartItem.getQuantity());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            preparedItems.add(buildOrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity(),
                    subtotal
            ));
        }

        for (CartItemResponse cartItem : cart.getItems()) {
            productClient.reduceStock(cartItem.getProductId(), cartItem.getQuantity());
        }

        for (OrderItem item : preparedItems) {
            order.addItem(item);
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        cartClient.clearCart(userId);

        return orderMapper.toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId) {
        return orderMapper.toDto(findOrderEntityById(orderId));
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByIdForUser(UUID orderId, UUID currentUserId) {
        Order order = findOrderEntityById(orderId);

        if (!order.getUserId().equals(currentUserId)) {
            throw new BaseException(
                    "You are not allowed to access this order",
                    ErrorCode.ACCESS_DENIED
            );
        }

        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getUserOrders(UUID userId, Pageable pageable) {
        return orderRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId, UUID userId) {
        Order order = findOrderEntityById(orderId);

        if (!order.getUserId().equals(userId)) {
            throw new BaseException(
                    "Order does not belong to this user",
                    ErrorCode.ACCESS_DENIED
            );
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BaseException(
                    "Order cannot be cancelled in status: " + order.getStatus(),
                    ErrorCode.ORDER_CANNOT_BE_CANCELLED
            );
        }

        for (OrderItem item : order.getItems()) {
            productClient.restoreStock(item.getProductId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponseDto updateStatus(UUID orderId, OrderStatus nextStatus) {
        Order order = findOrderEntityById(orderId);

        validateStatusTransition(order.getStatus(), nextStatus);

        order.setStatus(nextStatus);
        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByIdInternal(UUID orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        return orderMapper.toDto(order);
    }

    @Transactional
    public void updateOrderStatusInternal(UUID orderId, OrderStatus newStatus) {
        Order order = findOrderByIdOrThrow(orderId);
        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    private void validateStock(ProductResponse product, Integer quantity) {
        if (product.getStock() < quantity) {
            throw new BaseException(
                    "Insufficient stock for product: " + product.getName(),
                    ErrorCode.INSUFFICIENT_STOCK
            );
        }
    }

    private OrderItem buildOrderItem(
            UUID productId,
            String productName,
            BigDecimal price,
            Integer quantity,
            BigDecimal subtotal
    ) {
        return OrderItem.builder()
                .productId(productId)
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .subtotal(subtotal)
                .build();
    }

    private String buildFullAddress(String country, String city, String street) {
        return String.format("%s, %s, %s", country, city, street);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        boolean valid = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED
                    || newStatus == OrderStatus.PAID
                    || newStatus == OrderStatus.PAYMENT_FAILED
                    || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED -> newStatus == OrderStatus.PAID
                    || newStatus == OrderStatus.PAYMENT_FAILED
                    || newStatus == OrderStatus.SHIPPED
                    || newStatus == OrderStatus.CANCELLED;

            case PAID -> newStatus == OrderStatus.SHIPPED
                    || newStatus == OrderStatus.CANCELLED;

            case SHIPPED -> newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED, PAYMENT_FAILED -> false;
        };

        if (!valid) {
            throw new BaseException(
                    "Invalid order status transition: " + currentStatus + " -> " + newStatus,
                    ErrorCode.VALIDATION_FAILED
            );
        }
    }

    private Order findOrderEntityById(UUID orderId) {
        return orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new BaseException(
                        "Order not found with id: " + orderId,
                        ErrorCode.ORDER_NOT_FOUND
                ));
    }

    private Order findOrderByIdOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(
                        "Order not found with id: " + orderId,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));
    }
}