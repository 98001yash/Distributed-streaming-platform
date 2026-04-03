package com.distributed_streaming_platform.video_processing_service.exceptions;

public class FileUploadException extends BaseException {

    public FileUploadException(String message) {
        super(message, "FILE_UPLOAD_FAILED", 500);
    }
}