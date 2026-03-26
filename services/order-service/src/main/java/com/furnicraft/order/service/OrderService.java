package com.furnicraft.order.service;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.order.client.ProductClient;
import com.furnicraft.order.client.UserClient;
import com.furnicraft.order.client.response.ProductResponse;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final UserClient userClient;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {

        var address = userClient.getAddressById(request.getUserId(), request.getAddressId());
        String fullAddress = String.format("%s, %s, %s", address.getCountry(), address.getCity(), address.getStreet());

        Order order = Order.builder()
                .userId(request.getUserId())
                .shippingAddress(fullAddress)
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (var itemRequest : request.getItems()) {
            ProductResponse product = productClient.getProductById(itemRequest.getProductId());

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BaseException(
                        "Insufficient stock for product: " + product.getName(),
                        ErrorCode.INSUFFICIENT_STOCK
                );
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.addItem(orderItem);
            productClient.reduceStock(itemRequest.getProductId(), itemRequest.getQuantity());
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID id) {
        Order order = findOrderEntityById(id);
        return orderMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getUserOrders(UUID userId, Pageable pageable) {
        return orderRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID id, UUID userId) {
        Order order = findOrderEntityById(id);

        if (!order.getUserId().equals(userId)) {
            throw new BaseException(
                    "Order does not belong to this user",
                    ErrorCode.RESOURCE_NOT_FOUND
            );
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BaseException(
                    "Order cannot be cancelled in status: " + order.getStatus(),
                    ErrorCode.ORDER_CANNOT_BE_CANCELLED
            );
        }

        order.getItems().forEach(item ->
                productClient.restoreStock(item.getProductId(), item.getQuantity())
        );

        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponseDto updateStatus(UUID id, OrderStatus status) {
        Order order = findOrderEntityById(id);

        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        return orderMapper.toDto(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            default -> false;
        };

        if (!valid) {
            throw new BaseException(
                    "Invalid status transition: " + current + " → " + next,
                    ErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    Order findOrderEntityById(UUID id) {
        return orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseException("Order not found with id: " + id,
                        ErrorCode.RESOURCE_NOT_FOUND));
    }
}
