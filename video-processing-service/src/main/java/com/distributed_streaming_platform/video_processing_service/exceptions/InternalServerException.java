package com.distributed_streaming_platform.video_processing_service.exceptions;

public class InternalServerException extends BaseException {

    public InternalServerException(String message) {
        super(message, "INTERNAL_SERVER_ERROR", 500);
    }
}