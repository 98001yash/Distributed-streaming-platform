package com.distributed_streaming_platform.streaming_service.exceptions;


public class StorageAccessException extends StreamingException {

    public StorageAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}