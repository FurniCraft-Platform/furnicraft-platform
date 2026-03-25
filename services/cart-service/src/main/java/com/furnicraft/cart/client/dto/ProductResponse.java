package com.furnicraft.cart.client.dto;

import com.furnicraft.cart.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse {
    private UUID id;
    private String code;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status;
}