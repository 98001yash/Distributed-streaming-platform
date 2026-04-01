package com.distributed_streaming_platform.content_ingestion_service.exceptions;

public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException(Long contentId) {
        super("Content not found with id: " + contentId);
    }
}