package com.example.microservice.dto;

import lombok.Data;

@Data
public class GroupApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object code;
}
