package com.furnicraft.product.service;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.product.dto.category.CategoryRequestDto;
import com.furnicraft.product.dto.category.CategoryResponseDto;
import com.furnicraft.product.entity.Category;
import com.furnicraft.product.mapper.CategoryMapper;
import com.furnicraft.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BaseException("The category with this name is already exists" + request.getName(),
                    ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAllByIsDeletedFalse()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryByID(UUID id) {
        Category category = findCategoryEntityById(id);
        return categoryMapper.toDto(category);
    }

    protected Category findCategoryEntityById(UUID id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseException("The category with this id is not exists",
                        ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto request) {
        Category category = findCategoryEntityById(id);

        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BaseException("Category already exists with name: " + request.getName(),
                    ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        categoryMapper.updateEntity(request, category);
        return categoryMapper.toDto(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = findCategoryEntityById(id);
        category.setDeleted(true);
    }

}
