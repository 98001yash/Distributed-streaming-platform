package com.distributed_streaming_platform.video_processing_service.advices;

import com.distributed_streaming_platform.video_processing_service.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(BaseException ex) {

        log.error("BaseException occurred: errorCode={}, message={}",
                ex.getErrorCode(), ex.getMessage(), ex);

        Map<String, Object> response = buildResponse(
                ex.getMessage(),
                ex.getErrorCode(),
                ex.getStatus()
        );

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus()));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("Invalid argument: {}", ex.getMessage(), ex);

        Map<String, Object> response = buildResponse(
                ex.getMessage(),
                "INVALID_ARGUMENT",
                400
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        log.warn("Validation failed: {}", ex.getMessage(), ex);

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = buildResponse(
                "Validation failed",
                "VALIDATION_ERROR",
                400
        );

        response.put("errors", fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        log.error("Unhandled exception occurred", ex);

        Map<String, Object> response = buildResponse(
                "Something went wrong",
                "INTERNAL_SERVER_ERROR",
                500
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private Map<String, Object> buildResponse(String message, String errorCode, int status) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("status", status);

        return response;
    }
}
