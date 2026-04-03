package com.furnicraft.media.controller;

import com.furnicraft.common.dto.MediaResponse;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import com.furnicraft.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/products/{productId}")
    @PreAuthorize("hasAuthority('MEDIA_WRITE')")
    public MediaResponse uploadProductMedia(
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary
    ) {
        return mediaService.uploadProductMedia(productId, file, isPrimary);
    }

    @PostMapping("/users/{userId}/profile-image")
    @PreAuthorize("hasRole('ADMIN') or @authz.isCurrentUser(#userId)")
    public MediaResponse uploadProfileImage(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file
    ) {
        return mediaService.uploadUserProfileImage(userId, file);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<MediaResponse> getMediaByOwner(
            @RequestParam MediaOwnerType ownerType,
            @RequestParam UUID ownerId
    ) {
        return mediaService.getMediaByOwner(ownerType, ownerId);
    }

    @DeleteMapping("/{mediaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMedia(@PathVariable UUID mediaId) {
        mediaService.deleteMedia(mediaId);
    }
}