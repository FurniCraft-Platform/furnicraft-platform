package com.furnicraft.security.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.furnicraft.security.auth.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Set.of(
            PRODUCT_READ,
            USER_READ, USER_WRITE,
            ADDRESS_READ, ADDRESS_WRITE,
            CART_READ, CART_WRITE,
            ORDER_READ, ORDER_WRITE,
            PAYMENT_READ, PAYMENT_WRITE,
            MEDIA_READ, MEDIA_WRITE
    )),
    ADMIN(Set.of(
            PRODUCT_READ, PRODUCT_WRITE,
            CATEGORY_WRITE,
            USER_READ, USER_WRITE,
            ADDRESS_READ, ADDRESS_WRITE,
            CART_READ, CART_WRITE,
            ORDER_READ, ORDER_WRITE, ORDER_MANAGE,
            PAYMENT_READ, PAYMENT_WRITE, PAYMENT_MANAGE,
            MEDIA_READ, MEDIA_WRITE
    ));

    private final Set<Permission> permissions;
}
