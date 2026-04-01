package com.distributed_streaming_platform.content_ingestion_service.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoMetadata {

    private Long contentId;
    private String fileName;
    private String objectKey;
    private String storageUrl;
}
