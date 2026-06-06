package com.example.microservice.services.Dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePostRequest {
    private Long actorId;
    private String content;
    private String visibility;
    private List<PostMediaRequest> media;
}
