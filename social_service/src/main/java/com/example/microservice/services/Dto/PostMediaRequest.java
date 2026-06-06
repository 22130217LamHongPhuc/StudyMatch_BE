package com.example.microservice.services.Dto;

import lombok.Data;

@Data
public class PostMediaRequest {
    private String mediaUrl;
    private String mediaType;
}
