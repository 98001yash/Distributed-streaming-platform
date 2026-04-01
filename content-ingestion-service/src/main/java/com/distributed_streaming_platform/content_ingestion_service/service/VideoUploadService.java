package com.distributed_streaming_platform.content_ingestion_service.service;

import com.distributed_streaming_platform.content_ingestion_service.dtos.VideoUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VideoUploadService {

    VideoUploadResponse uploadVideo(Long contentId,
                                    MultipartFile file);
}
