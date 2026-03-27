package com.distributed_streaming_platform.user_service.advices;


import com.distributed_streaming_platform.user_service.exceptions.BadRequestException;
import com.distributed_streaming_platform.user_service.exceptions.ForbiddenOperationException;
import com.distributed_streaming_platform.user_service.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex,
                                                       HttpServletRequest request) {

        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);

    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UserNotFoundException ex,
                                                       HttpServletRequest request){
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);

    }

    public ResponseEntity<ApiError> handleForbidden (ForbiddenOperationException ex,
                                                     HttpServletRequest request){
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest( BadRequestException ex,
                                                      HttpServletRequest request) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric( Exception ex,
                                                   HttpServletRequest request) {
        return buildResponse("Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


        private ResponseEntity<ApiError> buildResponse(
                String message,
                HttpStatus status,
                HttpServletRequest request) {


            ApiError error = ApiError.builder()
                    .message(message)
                    .status(status.value())
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(error, status);

        }

}
