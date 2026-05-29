package com.example.microservice.exception;

import com.example.microservice.config.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("[VideoCall][BE][exception][not-found] message={}", ex.getMessage());
        APIResponse<?> response = new APIResponse<>(
                404,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[VideoCall][BE][exception][bad-request] message={}", ex.getMessage(), ex);
        APIResponse<?> response = new APIResponse<>(
                400,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<?>> handleException(Exception ex) {
        log.error("[VideoCall][BE][exception][internal] message={}", ex.getMessage(), ex);
        APIResponse<?> response = new APIResponse<>(
                500,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }



}
