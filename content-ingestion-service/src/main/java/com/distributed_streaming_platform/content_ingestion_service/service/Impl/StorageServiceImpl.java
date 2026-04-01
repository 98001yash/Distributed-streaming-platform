package com.distributed_streaming_platform.content_ingestion_service.service.Impl;

import com.distributed_streaming_platform.content_ingestion_service.exceptions.StorageException;
import com.distributed_streaming_platform.content_ingestion_service.service.StorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


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

            ensureBucketExists();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String fileUrl = getFileUrl(objectKey);

            log.info("File uploaded successfully objectKey={}", objectKey);

            return fileUrl;

        } catch (Exception ex) {
            log.error("MinIO upload failed objectKey={}", objectKey, ex);
            throw new StorageException("Failed to upload file", ex);
        }
    }


    @Override
    public void deleteFile(String objectKey) {

        try {
            log.info("Deleting file from MinIO objectKey={}", objectKey);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );

        } catch (Exception ex) {
            log.error("Failed to delete file objectKey={}", objectKey, ex);
            throw new StorageException("Failed to delete file", ex);
        }
    }

    @Override
    public String getFileUrl(String objectKey) {
        return String.format("%s/%s/%s", minioUrl, bucketName, objectKey);
    }


    private void ensureBucketExists() {

        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                log.info("Bucket not found, creating bucket={}", bucketName);

                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }

        } catch (Exception ex) {
            log.error("Bucket check/create failed", ex);
            throw new StorageException("Failed to initialize bucket", ex);
        }
    }
}
