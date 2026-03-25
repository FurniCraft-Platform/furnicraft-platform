package com.furnicraft.cart.mapper;

import com.furnicraft.cart.dto.CartItemResponse;
import com.furnicraft.cart.dto.CartResponse;
import com.furnicraft.cart.entity.Cart;
import com.furnicraft.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);

    @Mapping(target = "items", source = "items")
    CartResponse toCartResponse(Cart cart, List<CartItemResponse> items);

}
