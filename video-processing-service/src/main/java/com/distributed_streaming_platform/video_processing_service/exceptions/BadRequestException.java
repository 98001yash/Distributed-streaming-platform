package com.distributed_streaming_platform.video_processing_service.exceptions;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(message, "BAD_REQUEST", 400);
    }
}
