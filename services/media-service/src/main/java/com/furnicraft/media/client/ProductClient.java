package com.furnicraft.media.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductClient {

    @GetMapping("/{productId}")
    Object getProductById(@PathVariable UUID productId);
}