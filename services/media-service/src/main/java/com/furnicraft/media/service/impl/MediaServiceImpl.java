package com.furnicraft.media.service.impl;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.media.dto.StoredObject;
import com.furnicraft.media.entity.Media;
import com.furnicraft.media.entity.enums.MediaContentType;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import com.furnicraft.media.repository.MediaRepository;
import com.furnicraft.media.service.MediaService;
import com.furnicraft.media.service.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final StorageService storageService;
    // TODO: hazır olan repository-ləri inject et
    // private final ProductRepository productRepository;
    // private final UserRepository userRepository;

    @Override
    public Media uploadProductMedia(UUID productId, MultipartFile file, boolean isPrimary) {
        validateFile(file);
        validateProductExists(productId);

        if (isPrimary) {
            unsetCurrentPrimary(MediaOwnerType.PRODUCT, productId);
        }

        String objectKey = generateObjectKey(MediaOwnerType.PRODUCT, productId, file);
        StoredObject storedObject = storageService.upload(file, objectKey);

        Media media = Media.builder()
                .ownerType(MediaOwnerType.PRODUCT)
                .ownerId(productId)
                .contentType(resolveContentType(file))
                .originalFileName(storedObject.getOriginalFileName())
                .objectKey(storedObject.getObjectKey())
                .bucket(storedObject.getBucket())
                .size(storedObject.getSize())
                .isPrimary(isPrimary)
                .build();

        return mediaRepository.save(media);
    }

    @Override
    public Media uploadUserProfileImage(UUID userId, MultipartFile file) {
        validateFile(file);
        validateUserExists(userId);

        unsetCurrentPrimary(MediaOwnerType.USER, userId);

        String objectKey = generateObjectKey(MediaOwnerType.USER, userId, file);
        StoredObject storedObject = storageService.upload(file, objectKey);

        Media media = Media.builder()
                .ownerType(MediaOwnerType.USER)
                .ownerId(userId)
                .contentType(resolveContentType(file))
                .originalFileName(storedObject.getOriginalFileName())
                .objectKey(storedObject.getObjectKey())
                .bucket(storedObject.getBucket())
                .size(storedObject.getSize())
                .isPrimary(true)
                .build();

        return mediaRepository.save(media);
    }

    @Override
    @Transactional
    public List<Media> getMediaByOwner(MediaOwnerType ownerType, UUID ownerId) {
        return mediaRepository.findByOwnerTypeAndOwnerIdOrderByCreatedAtDesc(ownerType, ownerId);
    }

    @Override
    public void deleteMedia(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new BaseException("Media not found", ErrorCode.RESOURCE_NOT_FOUND));

        storageService.delete(media.getObjectKey());
        mediaRepository.delete(media);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException("File must not be empty", ErrorCode.VALIDATION_FAILED);
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new BaseException("File content type is missing", ErrorCode.VALIDATION_FAILED);
        }

        boolean supported = contentType.startsWith("image/") || contentType.startsWith("video/");
        if (!supported) {
            throw new BaseException("Only image and video files are allowed", ErrorCode.VALIDATION_FAILED);
        }
    }

    private MediaContentType resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType != null && contentType.startsWith("image/")) {
            return MediaContentType.IMAGE;
        }

        if (contentType != null && contentType.startsWith("video/")) {
            return MediaContentType.VIDEO;
        }

        throw new BaseException("Unsupported file content type", ErrorCode.VALIDATION_FAILED);
    }

    private String generateObjectKey(MediaOwnerType ownerType, UUID ownerId, MultipartFile file) {
        String originalFileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().replaceAll("\\s+", "_")
                : "file";

        return ownerType.name().toLowerCase()
                + "/"
                + ownerId
                + "/"
                + UUID.randomUUID()
                + "-"
                + originalFileName;
    }

    private void unsetCurrentPrimary(MediaOwnerType ownerType, UUID ownerId) {
        mediaRepository.findByOwnerTypeAndOwnerIdAndIsPrimaryTrue(ownerType, ownerId)
                .ifPresent(existing -> {
                    existing.setIsPrimary(false);
                    mediaRepository.save(existing);
                });
    }

    private void validateProductExists(UUID productId) {
        // TODO: ProductRepository hazır olanda bunu aktiv et
        // if (!productRepository.existsById(productId)) {
        //     throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "Product not found");
        // }
    }

    private void validateUserExists(UUID userId) {
        // TODO: UserRepository hazır olanda bunu aktiv et
        // if (!userRepository.existsById(userId)) {
        //     throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND, "User not found");
        // }
    }
}
