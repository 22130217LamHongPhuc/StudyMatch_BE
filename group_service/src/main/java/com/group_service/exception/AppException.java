package com.group_service.exception;

import com.group_service.enums.StatusCode;
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

    public AppException(StatusCode statusCode) {
        this.code = statusCode;
    }
}
