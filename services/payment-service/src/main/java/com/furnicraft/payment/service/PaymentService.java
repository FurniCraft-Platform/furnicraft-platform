package com.furnicraft.payment.service;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.payment.client.OrderClient;
import com.furnicraft.payment.dto.PaymentRequestDto;
import com.furnicraft.payment.dto.PaymentResponseDto;
import com.furnicraft.payment.entity.Payment;
import com.furnicraft.payment.enums.PaymentStatus;
import com.furnicraft.payment.mapper.PaymentMapper;
import com.furnicraft.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment initiated. paymentId={}, orderId={}", saved.getId(), saved.getOrderId());

        return processPayment(saved);
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
        Payment payment = findByIdOrThrow(paymentId);

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BaseException(
                    "Cannot refund payment with status: " + payment.getStatus(),
                    ErrorCode.PAYMENT_CANNOT_BE_REFUNDED
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        log.info("Payment refunded. paymentId={}, orderId={}", paymentId, payment.getOrderId());

        try {
            orderClient.updateOrderStatus(payment.getOrderId(), "CANCELLED");
            log.info("Order status updated to CANCELLED. orderId={}", payment.getOrderId());
        } catch (Exception e) {
            log.error("Failed to update order status after refund. orderId={}", payment.getOrderId(), e);
        }

        return paymentMapper.toDto(payment);
    }


    private PaymentResponseDto processPayment(Payment payment) {

        try {
            String transactionId = "TXN-" + UUID.randomUUID().toString().toUpperCase().substring(0, 12);

            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(transactionId);
            payment.setPaidAt(LocalDateTime.now());

            log.info("Payment completed. paymentId={}, transactionId={}", payment.getId(), transactionId);

            try {
                orderClient.updateOrderStatus(payment.getOrderId(), "CONFIRMED");
                log.info("Order status updated to CONFIRMED. orderId={}", payment.getOrderId());
            } catch (Exception e) {
                log.error("Failed to update order status after payment. orderId={}", payment.getOrderId(), e);
            }

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            log.error("Payment failed. paymentId={}", payment.getId(), e);
        }

        return paymentMapper.toDto(payment);
    }

    private Payment findByIdOrThrow(UUID paymentId) {
        return paymentRepository.findByIdAndIsDeletedFalse(paymentId)
                .orElseThrow(() -> new BaseException(
                        "Payment not found with id: " + paymentId,
                        ErrorCode.PAYMENT_NOT_FOUND
                ));
    }
}