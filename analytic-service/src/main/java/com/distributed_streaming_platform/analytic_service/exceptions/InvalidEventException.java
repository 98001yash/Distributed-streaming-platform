package com.distributed_streaming_platform.analytic_service.exceptions;


public class InvalidEventException extends AnalyticsException {

    public InvalidEventException(String message) {
        super(message);
    }
}