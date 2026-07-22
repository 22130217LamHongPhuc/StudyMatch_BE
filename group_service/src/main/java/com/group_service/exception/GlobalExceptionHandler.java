package com.group_service.exception;


import com.group_service.dto.ApiResponse;
import com.group_service.enums.StatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ApiResponse<String> handleAppException(AppException ex) {
        return new ApiResponse<>(false,ex.getCode() , ex.getMessage(),null);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public org.springframework.http.ResponseEntity<ApiResponse<String>> handleResponseStatusException(org.springframework.web.server.ResponseStatusException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(ex.getReason());
        apiResponse.setCode(null);
        return org.springframework.http.ResponseEntity
                .status(ex.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex){
        return new ApiResponse<>(false, StatusCode.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), null);
    }



}
