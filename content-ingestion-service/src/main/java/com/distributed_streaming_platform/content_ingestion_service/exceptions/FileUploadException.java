package com.distributed_streaming_platform.content_ingestion_service.exceptions;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
}
