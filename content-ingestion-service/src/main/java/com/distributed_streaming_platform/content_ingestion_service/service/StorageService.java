package com.distributed_streaming_platform.content_ingestion_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(String objectKey, MultipartFile file);
}