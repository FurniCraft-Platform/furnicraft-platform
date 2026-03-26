package com.furnicraft.media.service.impl;

import com.furnicraft.common.exception.BaseException;
import com.furnicraft.common.exception.ErrorCode;
import com.furnicraft.media.config.MinioProperties;
import com.furnicraft.media.dto.StoredObject;
import com.furnicraft.media.service.StorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements StorageService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public StoredObject upload(MultipartFile file, String objectKey) {
        try {
            createBucketIfNotExists(minioProperties.getBucket());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return StoredObject.builder()
                    .bucket(minioProperties.getBucket())
                    .objectKey(objectKey)
                    .originalFileName(file.getOriginalFilename())
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .build();

        } catch (Exception e) {
            throw new BaseException("Failed to upload file to MinIO", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(String objectKey) {
        try{
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e){
            throw new BaseException("Failed to remove file from MinIO", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            throw new BaseException("Failed to check if bucket existence", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void createBucketIfNotExists(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (BaseException e){
            throw e;
        } catch (Exception e) {
            throw new BaseException("Failed to create bucket", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


}
