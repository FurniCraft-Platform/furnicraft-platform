package com.furnicraft.media.repository;

import com.furnicraft.media.entity.Media;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findByOwnerTypeAndOwnerId(MediaOwnerType ownerType, UUID ownerId);

    List<Media> findByOwnerTypeAndOwnerIdOrderByCreatedAtDesc(MediaOwnerType ownerType, UUID ownerId);

    Optional<Media> findByOwnerTypeAndOwnerIdAndIsPrimaryTrue(MediaOwnerType ownerType, UUID ownerId);
}
