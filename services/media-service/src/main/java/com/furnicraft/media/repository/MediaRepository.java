package com.furnicraft.media.repository;

import com.furnicraft.media.entity.Media;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findByOwnerTypeAndOwnerId(MediaOwnerType ownerType, UUID ownerId);

    List<Media> findByOwnerTypeAndOwnerIdOrderByCreatedAtDesc(MediaOwnerType ownerType, UUID ownerId);

    Optional<Media> findByOwnerTypeAndOwnerIdAndIsPrimaryTrue(MediaOwnerType ownerType, UUID ownerId);
}
