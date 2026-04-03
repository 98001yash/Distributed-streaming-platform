package com.distributed_streaming_platform.video_processing_service.exceptions;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resource) {
        super(resource + " not found", "RESOURCE_NOT_FOUND", 404);
    }
}
