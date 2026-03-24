package com.furnicraft.product.dto.category;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {
    private UUID id;
    private String name;
    private String description;
}
