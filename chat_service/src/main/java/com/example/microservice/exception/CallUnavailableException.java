package com.example.microservice.exception;

import lombok.Getter;

@Getter
public class CallUnavailableException extends RuntimeException {
    private final String code;

    public CallUnavailableException(String code, String message) {
        super(message);
        this.code = code;
    }
}
