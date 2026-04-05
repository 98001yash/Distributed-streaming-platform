package com.distributed_streaming_platform.content_ingestion_service.service.Impl;


import com.distributed_streaming_platform.content_ingestion_service.service.StorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Override
    public String uploadFile(String objectKey, MultipartFile file) {

        try {
            log.info("Uploading file to MinIO objectKey={}", objectKey);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return String.format("%s/%s/%s", minioUrl, bucketName, objectKey);

        } catch (Exception ex) {
            log.error("Upload failed objectKey={}", objectKey, ex);
            throw new RuntimeException("Failed to upload file", ex);
        }
    }
}