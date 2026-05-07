package com.example.microservice.exception;

import com.example.microservice.enums.StatusCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    StatusCode code;
    public AppException(String message, StatusCode code) {
        super(message);
        this.code = code;
    }

    public AppException(String message) {
        super(message);
        this.code = StatusCode.INTERNAL_SERVER_ERROR;
    }
}
