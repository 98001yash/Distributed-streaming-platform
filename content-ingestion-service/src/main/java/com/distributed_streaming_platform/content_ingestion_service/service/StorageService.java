package com.distributed_streaming_platform.content_ingestion_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StorageService {

    File downloadToLocal(String objectKey);

    String uploadFile(String objectKey, File file);
}