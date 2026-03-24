package com.furnicraft.media.entity;

import com.furnicraft.common.entity.BaseEntity;
import com.furnicraft.media.entity.enums.MediaContentType;
import com.furnicraft.media.entity.enums.MediaOwnerType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaOwnerType ownerType;

    @Column(nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaContentType contentType;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String objectKey;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Boolean isPrimary;
}
