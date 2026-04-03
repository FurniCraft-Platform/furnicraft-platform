package com.furnicraft.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredObject {
    private String bucket;
    private String objectKey;
    private String originalFileName;
    private Long size;
    private String contentType;
}