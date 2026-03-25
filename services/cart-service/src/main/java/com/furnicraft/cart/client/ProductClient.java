package com.furnicraft.cart.client;

import com.furnicraft.cart.client.dto.ProductResponse;
import com.furnicraft.common.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/v1/products/{productId}")
    ApiResponse<ProductResponse> getProductById(@PathVariable UUID productId);
}