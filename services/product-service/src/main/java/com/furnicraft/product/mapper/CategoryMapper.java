package com.furnicraft.product.mapper;

import com.furnicraft.product.dto.category.CategoryRequestDto;
import com.furnicraft.product.dto.category.CategoryResponseDto;
import com.furnicraft.product.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "isDeleted", ignore = true)
    Category toEntity(CategoryRequestDto requestDto);

    CategoryResponseDto toDto(Category category);

    @Mapping(target = "deleted", ignore = true)
    void updateEntity(CategoryRequestDto requestDto, @MappingTarget Category category);
}