package com.distributed_streaming_platform.video_processing_service.exceptions;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", 403);
    }
}