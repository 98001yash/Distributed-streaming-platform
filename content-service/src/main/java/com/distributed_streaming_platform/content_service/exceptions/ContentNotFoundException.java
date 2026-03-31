package com.distributed_streaming_platform.content_service.exceptions;

public class ContentNotFoundException extends RuntimeException{

    public ContentNotFoundException(Long id) {
        super("Content not found with id: "+id);
    }
}
