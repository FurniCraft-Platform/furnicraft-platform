package com.furnicraft.order.client;

import com.furnicraft.order.client.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "ms-product", path = "/api/v1/products")
public interface ProductClient {

    @GetMapping("/{productId}")
    ProductResponse getProductById(@PathVariable UUID productId);

    @PatchMapping("/{productId}/stock")
    ProductResponse updateStock(@PathVariable UUID productId, @RequestParam Integer quantity);

    @PatchMapping("/{productId}/stock/restore")
    void restoreStock(@PathVariable UUID productId, @RequestParam Integer quantity);

    @PatchMapping("/{productId}/stock/reduce")
    void reduceStock(@PathVariable UUID productId, @RequestParam Integer quantity);
}
