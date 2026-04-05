package com.distributed_streaming_platform.video_processing_service.service;



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
public class StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;


    public File downloadToLocal(String objectKey) {

        try {
            File tempFile = File.createTempFile("video-", ".mp4");

            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            )) {
                Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            return tempFile;

        } catch (Exception e) {
            log.error("Download failed objectKey={}", objectKey, e);
            throw new RuntimeException("Failed to download file", e);
        }
    }

    public String uploadFile(String objectKey, File file) {

        try (InputStream stream = new FileInputStream(file)) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(stream, file.length(), -1)
                            .contentType("video/mp4")
                            .build()
            );

            return String.format("%s/%s/%s", minioUrl, bucket, objectKey);

        } catch (Exception e) {
            log.error("Upload failed objectKey={}", objectKey, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}
