package com.furnicraft.product.mapper;

import com.furnicraft.product.dto.product.ProductRequestDto;
import com.furnicraft.product.dto.product.ProductResponseDto;
import com.furnicraft.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponseDto toDto(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Product toEntity(ProductRequestDto request);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(ProductRequestDto request, @MappingTarget Product product);
}
