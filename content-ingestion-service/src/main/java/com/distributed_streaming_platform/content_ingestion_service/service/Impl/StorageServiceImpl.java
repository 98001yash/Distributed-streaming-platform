package com.distributed_streaming_platform.content_ingestion_service.service.Impl;

import com.distributed_streaming_platform.content_ingestion_service.service.StorageService;
import org.springframework.web.multipart.MultipartFile;

public class StorageServiceImpl implements StorageService {


    @Override
    public String uploadFile(String objectKey, MultipartFile file) {
        return "";
    }

    @Override
    public void deleteFile(String objectKey) {

    }

    @Override
    public String getFileUrl(String objectKey) {
        return "";
    }
}
