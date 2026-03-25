package com.furnicraft.order.client.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer stock;
}