package com.distributed_streaming_platform.video_processing_service.exceptions;

public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final int status;

    public BaseException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatus() {
        return status;
    }
}
