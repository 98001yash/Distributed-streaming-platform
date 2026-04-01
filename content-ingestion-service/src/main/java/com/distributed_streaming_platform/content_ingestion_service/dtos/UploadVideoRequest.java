package com.distributed_streaming_platform.content_ingestion_service.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadVideoRequest {

    @NotNull(message = "Content ID is required")
    private Long contentId;

}