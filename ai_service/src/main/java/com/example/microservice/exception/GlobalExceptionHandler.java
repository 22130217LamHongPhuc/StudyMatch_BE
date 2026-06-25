package com.example.microservice.exception;



import com.example.microservice.dto.admin.matching.ApiResponse;
import com.example.microservice.enums.StatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ApiResponse<String> handleAppException(AppException ex) {
        return new ApiResponse<>(false,ex.getCode() , ex.getMessage(),null);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex){
        return new ApiResponse<>(false, StatusCode.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), null);
    }



}
