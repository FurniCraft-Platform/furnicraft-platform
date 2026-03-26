package com.furnicraft.payment.controller;

import com.furnicraft.payment.dto.PaymentRequestDto;
import com.furnicraft.payment.dto.PaymentResponseDto;
import com.furnicraft.payment.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_WRITE')")
    public ResponseEntity<PaymentResponseDto> initiatePayment(
            @Valid @RequestBody PaymentRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(request));
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public ResponseEntity<Page<PaymentResponseDto>> getUserPayments(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId, pageable));
    }

    @PatchMapping("/{paymentId}/refund")
    @PreAuthorize("hasAuthority('PAYMENT_MANAGE')")
    public ResponseEntity<PaymentResponseDto> refundPayment(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }
}