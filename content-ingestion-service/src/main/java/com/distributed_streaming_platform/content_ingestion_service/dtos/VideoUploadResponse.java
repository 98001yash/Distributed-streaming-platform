package com.distributed_streaming_platform.content_ingestion_service.dtos;

import com.distributed_streaming_platform.content_ingestion_service.enums.UploadStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VideoUploadResponse {

    private Long assetId;
    private Long contentId;
    private String fileName;
    private String storageUrl;
    private UploadStatus status;
    private LocalDateTime uploadedAt;
}