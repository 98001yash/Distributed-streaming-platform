package com.distributed_streaming_platform.content_ingestion_service.exceptions;

public class DuplicateUploadException extends RuntimeException {
    public DuplicateUploadException(Long contentId) {
        super("Video already uploaded for contentId: " + contentId);
    }
}