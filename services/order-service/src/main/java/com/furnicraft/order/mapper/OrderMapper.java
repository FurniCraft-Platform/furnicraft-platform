package com.furnicraft.order.mapper;

import com.furnicraft.order.dto.response.OrderItemResponseDto;
import com.furnicraft.order.dto.response.OrderResponseDto;
import com.furnicraft.order.entity.Order;
import com.furnicraft.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderResponseDto toDto(Order order);

    OrderItemResponseDto toItemDto(OrderItem orderItem);
}