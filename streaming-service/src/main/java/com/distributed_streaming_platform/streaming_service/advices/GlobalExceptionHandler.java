package com.distributed_streaming_platform.streaming_service.advices;


import com.distributed_streaming_platform.streaming_service.exceptions.StorageAccessException;
import com.distributed_streaming_platform.streaming_service.exceptions.UnauthorizedAccessException;
import com.distributed_streaming_platform.streaming_service.exceptions.VideoNotFoundException;
import com.distributed_streaming_platform.streaming_service.exceptions.VideoNotReadyException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleVideoNotFound(
            VideoNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(VideoNotReadyException.class)
    public ResponseEntity<ApiErrorResponse> handleVideoNotReady(
            VideoNotReadyException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            UnauthorizedAccessException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(StorageAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleStorage(
            StorageAccessException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
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
