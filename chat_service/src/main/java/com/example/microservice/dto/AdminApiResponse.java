package com.example.microservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public AdminApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
