package com.furnicraft.payment.service;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.payment.client.OrderClient;
import com.furnicraft.payment.client.dto.OrderResponse;
import com.furnicraft.payment.dto.PaymentRequestDto;
import com.furnicraft.payment.dto.PaymentResponseDto;
import com.furnicraft.payment.entity.Payment;
import com.furnicraft.payment.enums.OrderStatus;
import com.furnicraft.payment.enums.PaymentStatus;
import com.furnicraft.payment.mapper.PaymentMapper;
import com.furnicraft.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;

    @Transactional
    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {
        OrderResponse order = orderClient.getOrderById(request.getOrderId());

        validateOrderForPayment(order, request);

        boolean alreadyPaid = paymentRepository.existsByOrderIdAndStatus(
                request.getOrderId(), PaymentStatus.COMPLETED
        );
        if (alreadyPaid) {
            throw new BaseException(
                    "Payment already completed for order: " + request.getOrderId(),
                    ErrorCode.PAYMENT_ALREADY_COMPLETED
            );
        }

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .transactionId(generateTransactionId())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment initiated. paymentId={}, orderId={}", saved.getId(), saved.getOrderId());

        return processPayment(saved, request);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(UUID paymentId) {
        Payment payment = findByIdOrThrow(paymentId);
        return paymentMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new BaseException(
                        "Payment not found for order: " + orderId,
                        ErrorCode.PAYMENT_NOT_FOUND
                ));
        return paymentMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getUserPayments(UUID userId, Pageable pageable) {
        return paymentRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable)
                .map(paymentMapper::toDto);
    }

    @Transactional
    public PaymentResponseDto refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BaseException("Payment not found", ErrorCode.RESOURCE_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BaseException("Only completed payments can be refunded", ErrorCode.VALIDATION_FAILED);
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        orderClient.updateOrderStatus(payment.getOrderId(), OrderStatus.CANCELLED);

        return paymentMapper.toDto(payment);
    }

    @Transactional
    public PaymentResponseDto processPayment(Payment payment, PaymentRequestDto request) {
        try {
            boolean paymentSuccessful = simulatePayment(request);

            if (!paymentSuccessful) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                safeUpdateOrderStatus(payment.getOrderId(), OrderStatus.PAYMENT_FAILED);

                return paymentMapper.toDto(payment);
            }

            orderClient.updateOrderStatus(payment.getOrderId(), OrderStatus.PAID);

            payment.setStatus(PaymentStatus.COMPLETED);
            payment = paymentRepository.save(payment);

            return paymentMapper.toDto(payment);

        } catch (Exception ex) {
            log.error("Payment processing failed for orderId={}, paymentId={}",
                    payment.getOrderId(), payment.getId(), ex);

            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            try {
                safeUpdateOrderStatus(payment.getOrderId(), OrderStatus.PAYMENT_FAILED);
            } catch (Exception ignored) {
                log.warn("Failed to update order status to PAYMENT_FAILED for orderId={}", payment.getOrderId());
            }

            throw new BaseException("Payment processing failed", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private Payment findByIdOrThrow(UUID paymentId) {
        return paymentRepository.findByIdAndIsDeletedFalse(paymentId)
                .orElseThrow(() -> new BaseException(
                        "Payment not found with id: " + paymentId,
                        ErrorCode.PAYMENT_NOT_FOUND
                ));
    }

    private void validateOrderForPayment(OrderResponse order, PaymentRequestDto request) {
        if (order == null) {
            throw new BaseException("Order not found", ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().signum() <= 0) {
            throw new BaseException("Order amount is invalid", ErrorCode.VALIDATION_FAILED);
        }

        if ("PAID".equalsIgnoreCase(order.getStatus())) {
            throw new BaseException("Order is already paid", ErrorCode.VALIDATION_FAILED);
        }

        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new BaseException("Cancelled order cannot be paid", ErrorCode.VALIDATION_FAILED);
        }

        if (request.getUserId() != null && order.getUserId() != null && !request.getUserId().equals(order.getUserId())) {
            throw new BaseException("User does not match order owner", ErrorCode.VALIDATION_FAILED);
        }
    }

    private boolean simulatePayment(PaymentRequestDto request) {
        return true;
    }

    private void safeUpdateOrderStatus(UUID orderId, OrderStatus status) {
        orderClient.updateOrderStatus(orderId, status);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID();
    }
}