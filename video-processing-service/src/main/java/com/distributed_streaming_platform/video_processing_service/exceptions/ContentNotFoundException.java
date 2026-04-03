package com.distributed_streaming_platform.video_processing_service.exceptions;

public class ContentNotFoundException extends BaseException {

    public ContentNotFoundException(Long id) {
        super("Content not found with id: " + id, "CONTENT_NOT_FOUND", 404);
    }
}