package com.furnicraft.security.auth;

public enum Permission {
    PRODUCT_READ,
    PRODUCT_WRITE,
    CATEGORY_WRITE,

    USER_READ,
    USER_WRITE,

    ADDRESS_READ,
    ADDRESS_WRITE,

    CART_READ,
    CART_WRITE,

    ORDER_READ,
    ORDER_WRITE,
    ORDER_MANAGE,

    PAYMENT_READ,
    PAYMENT_WRITE,
    PAYMENT_MANAGE,

    MEDIA_READ,
    MEDIA_WRITE
}