package com.furnicraft.media.controller;

import com.furnicraft.common.dto.MediaResponse;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import com.furnicraft.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media/internal")
@RequiredArgsConstructor
public class InternalMediaController {

    private final MediaService mediaService;

    @GetMapping
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public ResponseEntity<List<MediaResponse>> getMediaByOwnerInternal(
            @RequestParam MediaOwnerType ownerType,
            @RequestParam UUID ownerId
    ) {
        return ResponseEntity.ok(mediaService.getMediaByOwner(ownerType, ownerId));
    }

    @PostMapping(value = "/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public ResponseEntity<MediaResponse> uploadProductMediaInternal(
            @PathVariable UUID productId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary
    ) {
        return ResponseEntity.ok(mediaService.uploadProductMedia(productId, file, isPrimary));
    }

    @PostMapping(value = "/users/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public ResponseEntity<MediaResponse> uploadUserProfileImageInternal(
            @PathVariable UUID userId,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(mediaService.uploadUserProfileImage(userId, file));
    }

    @DeleteMapping("/{mediaId}")
    @PreAuthorize("@internalAuth.isInternalRequest()")
    public ResponseEntity<Void> deleteMediaInternal(@PathVariable UUID mediaId) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.noContent().build();
    }
}