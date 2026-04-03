package com.furnicraft.product.service;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.product.client.MediaClient;
import com.furnicraft.common.dto.MediaResponse;
import com.furnicraft.product.dto.product.ProductRequestDto;
import com.furnicraft.product.dto.product.ProductResponseDto;
import com.furnicraft.product.entity.Category;
import com.furnicraft.product.entity.Product;
import com.furnicraft.product.enums.ProductStatus;
import com.furnicraft.product.mapper.ProductMapper;
import com.furnicraft.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final MediaClient mediaClient;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request) {
        if (productRepository.existsByCodeAndIsDeletedFalse(request.getCode())) {
            throw new BaseException("Product already exists with code: " + request.getCode(),
                    ErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        Category category = categoryService.findCategoryEntityById(request.getCategoryId());

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setStatus(ProductStatus.DRAFT);
        product.setStock(request.getStock());

        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAllByIsDeletedFalse(pageable)
                .map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(UUID id) {
        return productMapper.toDto(findProductEntityById(id));
    }

    @Transactional
    public ProductResponseDto updateProduct(UUID id, ProductRequestDto request) {
        Product product = findProductEntityById(id);

        if (productRepository.existsByCodeAndIdNotAndIsDeletedFalse(request.getCode(), id)) {
            throw new BaseException("Product code already in use: " + request.getCode(),
                    ErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryService.findCategoryEntityById(request.getCategoryId());
            product.setCategory(newCategory);
        }

        productMapper.updateEntity(request, product);
        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = findProductEntityById(id);
        product.setDeleted(true);
    }

    Product findProductEntityById(UUID id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BaseException("Product not found with id: " + id,
                        ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional
    public MediaResponse uploadProductMedia(UUID productId, MultipartFile file, Boolean isPrimary) {
        findProductEntityById(productId);
        return mediaClient.uploadProductMedia(productId, file, isPrimary);
    }

    @Transactional(readOnly = true)
    public List<MediaResponse> getProductMedia(UUID productId) {
        findProductEntityById(productId);
        return mediaClient.getProductMedia("PRODUCT", productId);
    }

    @Transactional
    public ProductResponseDto reduceStock(UUID id, Integer quantity) {
        Product product = findProductEntityById(id);

        if (product.getStock() < quantity) {
            throw new BaseException("Insufficient stock for product: " + id,
                    ErrorCode.INSUFFICIENT_STOCK);
        }

        product.setStock(product.getStock() - quantity);
        return productMapper.toDto(product);
    }

    @Transactional
    public ProductResponseDto restoreStock(UUID id, Integer quantity) {
        Product product = findProductEntityById(id);
        product.setStock(product.getStock() + quantity);
        return productMapper.toDto(product);
    }
}