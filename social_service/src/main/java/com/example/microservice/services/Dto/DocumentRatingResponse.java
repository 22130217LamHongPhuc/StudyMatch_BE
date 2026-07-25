package com.example.microservice.services.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentRatingResponse {
    private Long id;
    private Long documentId;
    private Long userId;
    private Integer score;
    private String review;
    private String userName;
    private String userAvatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
