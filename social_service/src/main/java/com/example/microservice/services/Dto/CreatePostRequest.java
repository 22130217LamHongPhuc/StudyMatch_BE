package com.example.microservice.services.Dto;

import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {
    private Long authorId;
    private String content;
    private String visibility;
    private List<PostMediaRequest> media;
}
