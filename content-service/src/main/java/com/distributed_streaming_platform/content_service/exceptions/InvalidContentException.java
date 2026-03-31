package com.distributed_streaming_platform.content_service.exceptions;

public class InvalidContentException extends RuntimeException {
    public InvalidContentException(String message) {
        super(message);
    }
}
