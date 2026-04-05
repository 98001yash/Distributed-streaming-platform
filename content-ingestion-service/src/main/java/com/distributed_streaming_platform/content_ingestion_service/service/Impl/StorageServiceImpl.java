package com.distributed_streaming_platform.content_ingestion_service.service.Impl;


import com.distributed_streaming_platform.content_ingestion_service.service.StorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public File downloadToLocal(String objectKey) {

        try {
            File tempFile = File.createTempFile("video-", ".mp4");

            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            )) {
                Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            return tempFile;

        } catch (Exception ex) {
            log.error("Download failed objectKey={}", objectKey, ex);
            throw new RuntimeException("Failed to download file", ex);
        }
    }

    @Override
    public String uploadFile(String objectKey, File file) {

        try (InputStream stream = new FileInputStream(file)) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(stream, file.length(), -1)
                            .contentType("video/mp4")
                            .build()
            );

            return String.format("%s/%s/%s", minioUrl, bucketName, objectKey);

        } catch (Exception ex) {
            log.error("Upload failed objectKey={}", objectKey, ex);
            throw new RuntimeException("Failed to upload file", ex);
        }
    }
}