package com.distributed_streaming_platform.video_processing_service.exceptions;

public class ConflictException extends BaseException {

    public ConflictException(String message) {
        super(message, "CONFLICT", 409);
    }
}