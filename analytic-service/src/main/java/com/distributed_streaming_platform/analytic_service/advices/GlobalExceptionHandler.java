package com.distributed_streaming_platform.analytic_service.advices;


import com.distributed_streaming_platform.analytic_service.exceptions.AnalyticsDataNotFoundException;
import com.distributed_streaming_platform.analytic_service.exceptions.AnalyticsStorageException;
import com.distributed_streaming_platform.analytic_service.exceptions.EventProcessingException;
import com.distributed_streaming_platform.analytic_service.exceptions.InvalidEventException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidEventException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidEvent(
            InvalidEventException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EventProcessingException.class)
    public ResponseEntity<ApiErrorResponse> handleProcessing(
            EventProcessingException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(AnalyticsStorageException.class)
    public ResponseEntity<ApiErrorResponse> handleStorage(
            AnalyticsStorageException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(AnalyticsDataNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            AnalyticsDataNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            Exception ex,
            HttpStatus status,
            HttpServletRequest request
    ) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, status);
    }
}