package com.distributed_streaming_platform.streaming_service.exceptions;

public class StreamingException extends RuntimeException {

    public StreamingException(String message) {
        super(message);
    }

    public StreamingException(String message, Throwable cause) {
        super(message, cause);
    }
}
