package com.example.microservice.services.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;
    private String code;
}

