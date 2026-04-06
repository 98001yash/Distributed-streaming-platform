package com.distributed_streaming_platform.streaming_service.exceptions;


public class UnauthorizedAccessException extends StreamingException {

    public UnauthorizedAccessException() {
        super("User is not authorized to access this video");
    }
}