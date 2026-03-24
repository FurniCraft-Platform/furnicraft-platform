package com.furnicraft.product.dto.product;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductRequestDto {
    @NotBlank(message = "Product code is required")
    @Size(max = 50, message = "Product code must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z]{2,10}-\\d{3}$", message = "Product code must be in format: SOFA-001")
    private String code;

    @NotBlank(message = "Product name s required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}
