package com.distributed_streaming_platform.video_processing_service.exceptions;

public class VideoProcessingException extends BaseException {

    public VideoProcessingException(String message) {
        super(message, "VIDEO_PROCESSING_FAILED", 500);
    }
}