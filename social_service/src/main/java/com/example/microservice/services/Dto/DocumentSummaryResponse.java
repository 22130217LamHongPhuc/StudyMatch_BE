package com.example.microservice.services.Dto;

import com.example.microservice.services.entity.DocumentCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private DocumentCategory category;
    private String fileType;
    private Long fileSize;
    private Long uploaderId;
    private String uploaderName;
    private String sourceName;
    private Long viewCount;
    private Long downloadCount;
    private Double averageRating;
    private Long ratingCount;
    private LocalDateTime createdAt;
}
