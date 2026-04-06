package com.distributed_streaming_platform.analytic_service.exceptions;


public class EventProcessingException extends AnalyticsException {

    public EventProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}