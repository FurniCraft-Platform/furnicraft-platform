package com.furnicraft.payment.mapper;

import com.furnicraft.payment.dto.PaymentResponseDto;
import com.furnicraft.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    PaymentResponseDto toDto(Payment payment);
}