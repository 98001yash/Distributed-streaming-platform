package com.distributed_streaming_platform.video_processing_service.exceptions;

public class InvalidFileFormatException extends BaseException {

    public InvalidFileFormatException(String message) {
        super(message, "INVALID_FILE_FORMAT", 400);
    }
}