package com.furnicraft.product.controller;

import com.furnicraft.common.dto.MediaResponse;
import com.furnicraft.product.dto.product.ProductRequestDto;
import com.furnicraft.product.dto.product.ProductResponseDto;
import com.furnicraft.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/stock/reduce")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    public ResponseEntity<ProductResponseDto> reduceStock(
            @PathVariable UUID productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.reduceStock(productId, quantity));
    }

    @PatchMapping("/{productId}/stock/restore")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    public ResponseEntity<ProductResponseDto> restoreStock(
            @PathVariable UUID productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.restoreStock(productId, quantity));
    }

    @PostMapping(value = "/{productId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    public ResponseEntity<MediaResponse> uploadProductMedia(
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false, defaultValue = "false") Boolean isPrimary
    ) {
        return ResponseEntity.ok(productService.uploadProductMedia(productId, file, isPrimary));
    }

    @GetMapping("/{productId}/media")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public ResponseEntity<List<MediaResponse>> getProductMedia(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductMedia(productId));
    }


}