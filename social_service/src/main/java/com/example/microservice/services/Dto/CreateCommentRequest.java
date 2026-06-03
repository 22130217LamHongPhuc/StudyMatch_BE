package com.example.microservice.services.Dto;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long authorId;
    private String content;
}
