package com.furnicraft.product.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MediaResponse {
    private UUID id;
    private String ownerType;
    private UUID ownerId;
    private String contentType;
    private String originalFileName;
    private String objectKey;
    private String bucket;
    private Long size;
    private Boolean isPrimary;
}
