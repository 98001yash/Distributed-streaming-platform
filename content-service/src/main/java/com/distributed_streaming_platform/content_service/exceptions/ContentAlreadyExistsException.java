package com.distributed_streaming_platform.content_service.exceptions;

public class ContentAlreadyExistsException extends RuntimeException {
    public ContentAlreadyExistsException(String message) {
        super(message);
    }
}
