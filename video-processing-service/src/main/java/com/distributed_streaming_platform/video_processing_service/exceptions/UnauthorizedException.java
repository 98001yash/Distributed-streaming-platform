package com.distributed_streaming_platform.video_processing_service.exceptions;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", 401);
    }
}