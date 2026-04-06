package com.distributed_streaming_platform.streaming_service.exceptions;


public class VideoNotReadyException extends StreamingException {

    public VideoNotReadyException(Long contentId) {
        super("Video not ready for streaming contentId=" + contentId);
    }
}