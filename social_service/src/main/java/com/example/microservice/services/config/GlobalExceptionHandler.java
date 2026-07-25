package com.example.microservice.services.config;

import com.example.microservice.services.exception.AppException;
import com.example.microservice.services.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        Map<String, Object> errorData = Map.of(
                "errorCode", errorCode.getCode(),
                "errorMessage", ex.getMessage()
        );
        APIResponse<Map<String, Object>> response = new APIResponse<>(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                errorData
        );
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );

        APIResponse response = new APIResponse(ResponseStatus.BAD_REQUEST,errors );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleGeneralException(Exception ex) {
        Map<String, Object> errorData = Map.of(
                "errorCode", "INTERNAL_SERVER_ERROR",
                "errorMessage", ex.getMessage()
        );
        APIResponse<Map<String, Object>> response = new APIResponse<>(
                500,
                "Internal Server Error",
                errorData
        );
        return ResponseEntity.status(500).body(response);
    }
}

