package com.furnicraft.media.service;

import com.furnicraft.common.dto.MediaResponse;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaService {

    MediaResponse uploadProductMedia(UUID productId, MultipartFile file, boolean isPrimary);

    MediaResponse uploadUserProfileImage(UUID userId, MultipartFile file);

    List<MediaResponse> getMediaByOwner(MediaOwnerType ownerType, UUID ownerId);

    void deleteMedia(UUID mediaId);
}