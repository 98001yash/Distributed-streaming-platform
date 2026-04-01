package com.distributed_streaming_platform.content_ingestion_service.exceptions;



public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String type) {
        super("Invalid file type: " + type + ". Only video files are allowed.");
    }
}
