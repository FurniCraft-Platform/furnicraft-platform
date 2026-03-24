package com.furnicraft.product.client;

import com.furnicraft.product.client.dto.MediaResponse;
import com.furnicraft.product.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "media-service", configuration = FeignMultipartSupportConfig.class)
public interface MediaClient {

    @PostMapping(value = "/api/v1/media/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    MediaResponse uploadProductMedia(
            @PathVariable("productId") UUID productId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("isPrimary") Boolean isPrimary
    );

    @GetMapping("/api/v1/media")
    List<MediaResponse> getProductMedia(
            @RequestParam("ownerType") String ownerType,
            @RequestParam("ownerId") UUID ownerId
    );
}