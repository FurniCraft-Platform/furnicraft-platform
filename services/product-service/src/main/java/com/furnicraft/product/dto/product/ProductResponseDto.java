package com.furnicraft.product.dto.product;

import com.furnicraft.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
public class ProductResponseDto {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status;
    private UUID categoryId;
    private String categoryName;
}
