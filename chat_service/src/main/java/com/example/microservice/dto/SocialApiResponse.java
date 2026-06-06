package com.example.microservice.dto;

import lombok.Data;

@Data
public class SocialApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private String timestamp;
}
