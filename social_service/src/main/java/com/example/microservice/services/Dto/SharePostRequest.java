package com.example.microservice.services.Dto;

import lombok.Data;

@Data
public class SharePostRequest {
    private Long authorId;
    private String content;
    private String visibility = "PUBLIC";
}
