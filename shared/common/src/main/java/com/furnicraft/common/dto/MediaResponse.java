package com.furnicraft.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResponse {
    private UUID mediaId;
    private UUID ownerId;
    private String ownerType;
    private String originalFileName;
    private String objectKey;
    private String bucket;
    private String contentType;
    private Long size;
    private Boolean isPrimary;
    private String url;
}