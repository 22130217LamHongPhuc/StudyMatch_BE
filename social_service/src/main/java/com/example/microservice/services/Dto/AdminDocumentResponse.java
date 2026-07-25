package com.example.microservice.services.Dto;

import com.example.microservice.services.entity.DocumentCategory;
import com.example.microservice.services.entity.DocumentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminDocumentResponse {
    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private DocumentCategory category;
    private String fileUrl;
    private String storageKey;
    private String originalFileName;
    private String fileType;
    private String mimeType;
    private Long fileSize;
    private Long uploaderId;
    private String uploaderName;
    private String sourceName;
    private DocumentStatus status;
    private String rejectionReason;
    private String hiddenReason;
    private Long viewCount;
    private Long downloadCount;
    private Double averageRating;
    private Long ratingCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private Long reviewerId;
    private LocalDateTime reviewedAt;
    private Long unresolvedReportCount;
}
