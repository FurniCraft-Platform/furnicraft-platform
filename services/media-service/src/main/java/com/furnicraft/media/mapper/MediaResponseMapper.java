package com.furnicraft.media.mapper;

import com.furnicraft.common.dto.MediaResponse;
import com.furnicraft.common.dto.StoredObject;
import com.furnicraft.media.entity.Media;
import org.springframework.stereotype.Component;

@Component
public class MediaResponseMapper {

    public MediaResponse toResponse(Media media, StoredObject storedObject, String url) {
        return MediaResponse.builder()
                .mediaId(media.getId())
                .ownerId(media.getOwnerId())
                .ownerType(media.getOwnerType().name())
                .originalFileName(
                        storedObject != null ? storedObject.getOriginalFileName() : media.getOriginalFileName()
                )
                .objectKey(
                        storedObject != null ? storedObject.getObjectKey() : media.getObjectKey()
                )
                .bucket(
                        storedObject != null ? storedObject.getBucket() : media.getBucket()
                )
                .contentType(
                        storedObject != null ? storedObject.getContentType() : media.getContentType().name()
                )
                .size(
                        storedObject != null ? storedObject.getSize() : media.getSize()
                )
                .isPrimary(media.getIsPrimary())
                .url(url)
                .build();
    }

    public MediaResponse toResponse(Media media, String url) {
        return MediaResponse.builder()
                .mediaId(media.getId())
                .ownerId(media.getOwnerId())
                .ownerType(media.getOwnerType().name())
                .originalFileName(media.getOriginalFileName())
                .objectKey(media.getObjectKey())
                .bucket(media.getBucket())
                .contentType(media.getContentType().name())
                .size(media.getSize())
                .isPrimary(media.getIsPrimary())
                .url(url)
                .build();
    }
}