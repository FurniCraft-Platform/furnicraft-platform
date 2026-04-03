package com.furnicraft.user.client;

import com.furnicraft.common.dto.MediaResponse;
import com.furnicraft.user.config.FeignMultipartSupportConfig;
import com.furnicraft.user.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "media-service",
        configuration = {
                FeignMultipartSupportConfig.class,
                InternalFeignConfig.class
        }
)
public interface MediaClient {

    @PostMapping(value = "/api/v1/media/internal/users/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    MediaResponse uploadUserProfileImage(
            @PathVariable("userId") UUID userId,
            @RequestPart("file") MultipartFile file
    );

    @GetMapping("/api/v1/media/internal")
    List<MediaResponse> getUserMedia(
            @RequestParam("ownerType") String ownerType,
            @RequestParam("ownerId") UUID ownerId
    );
}