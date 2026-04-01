package com.distributed_streaming_platform.content_ingestion_service.exceptions;

public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException(long size) {
        super("File size exceeds limit: " + size);
    }
}
