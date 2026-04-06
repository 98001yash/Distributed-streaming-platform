package com.distributed_streaming_platform.streaming_service.exceptions;


public class VideoNotFoundException extends StreamingException {

    public VideoNotFoundException(Long contentId) {
        super("Video not found for contentId=" + contentId);
    }
}