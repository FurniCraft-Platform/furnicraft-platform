package com.furnicraft.media.service;

import com.furnicraft.media.dto.StoredObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    StoredObject upload(MultipartFile file, String objectKey);

    void delete(String objectKey);

    boolean bucketExists(String bucketName);

    void createBucketIfNotExists(String bucketName);
}
