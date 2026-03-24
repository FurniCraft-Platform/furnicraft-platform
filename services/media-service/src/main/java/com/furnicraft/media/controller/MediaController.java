package com.furnicraft.media.controller;

import com.furnicraft.media.entity.Media;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import com.furnicraft.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/products/{productId}")
    public Media uploadProductMedia(
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary
    ) {
        return mediaService.uploadProductMedia(productId, file, isPrimary);
    }

    @PostMapping("/users/{userId}/profile-image")
    public Media uploadProfileImage(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file
    ) {
        return mediaService.uploadUserProfileImage(userId, file);
    }

    @GetMapping
    public List<Media> getMediaByOwner(
            @RequestParam MediaOwnerType ownerType,
            @RequestParam UUID ownerId
    ) {
        return mediaService.getMediaByOwner(ownerType, ownerId);
    }

    @DeleteMapping("/{mediaId}")
    public void deleteMedia(@PathVariable UUID mediaId) {
        mediaService.deleteMedia(mediaId);
    }
}
