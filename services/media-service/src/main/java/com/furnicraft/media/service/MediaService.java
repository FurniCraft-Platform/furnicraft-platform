package com.furnicraft.media.service;

import com.furnicraft.media.entity.Media;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaService {

    Media uploadProductMedia(UUID productId, MultipartFile file, boolean isPrimary);

    Media uploadUserProfileImage(UUID userId, MultipartFile file);

    List<Media> getMediaByOwner(MediaOwnerType ownerType, UUID ownerId);

    void deleteMedia(UUID mediaId);
}
