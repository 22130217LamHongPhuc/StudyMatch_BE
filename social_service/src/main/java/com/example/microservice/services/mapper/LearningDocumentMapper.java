package com.example.microservice.services.mapper;

import com.example.microservice.services.Dto.CreateLearningDocumentRequest;
import com.example.microservice.services.Dto.DocumentSummaryResponse;
import com.example.microservice.services.Dto.LearningDocumentResponse;
import com.example.microservice.services.Dto.DocumentRatingResponse;
import com.example.microservice.services.Dto.AdminDocumentResponse;
import com.example.microservice.services.entity.DocumentStatus;
import com.example.microservice.services.entity.LearningDocument;
import com.example.microservice.services.entity.DocumentRating;
import org.springframework.stereotype.Component;

@Component
public class LearningDocumentMapper {

    public LearningDocument toEntity(CreateLearningDocumentRequest request, Long uploaderId) {
        if (request == null) {
            return null;
        }
        LearningDocument entity = new LearningDocument();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setSubjectId(request.getSubjectId());
        entity.setCategory(request.getCategory());
        entity.setFileUrl(request.getFileUrl());
        entity.setStorageKey(request.getStorageKey());
        entity.setOriginalFileName(request.getOriginalFileName());
        entity.setFileType(request.getFileType());
        entity.setMimeType(request.getMimeType());
        entity.setFileSize(request.getFileSize());
        entity.setSourceName(request.getSourceName());

        entity.setUploaderId(uploaderId);
        entity.setStatus(DocumentStatus.PENDING);
        entity.setViewCount(0L);
        entity.setDownloadCount(0L);
        entity.setAverageRating(0.0);
        entity.setRatingCount(0L);
        entity.setRejectionReason(null);
        entity.setPublishedAt(null);

        return entity;
    }

    public LearningDocumentResponse toResponse(LearningDocument entity) {
        if (entity == null) {
            return null;
        }
        LearningDocumentResponse response = new LearningDocumentResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setSubjectId(entity.getSubjectId());
        response.setCategory(entity.getCategory());
        response.setFileUrl(entity.getFileUrl());
        response.setStorageKey(entity.getStorageKey());
        response.setOriginalFileName(entity.getOriginalFileName());
        response.setFileType(entity.getFileType());
        response.setMimeType(entity.getMimeType());
        response.setFileSize(entity.getFileSize());
        response.setUploaderId(entity.getUploaderId());
        response.setSourceName(entity.getSourceName());
        response.setStatus(entity.getStatus());
        response.setRejectionReason(entity.getRejectionReason());
        response.setViewCount(entity.getViewCount());
        response.setDownloadCount(entity.getDownloadCount());
        response.setAverageRating(entity.getAverageRating());
        response.setRatingCount(entity.getRatingCount());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setPublishedAt(entity.getPublishedAt());
        return response;
    }

    public DocumentSummaryResponse toSummaryResponse(LearningDocument entity) {
        if (entity == null) {
            return null;
        }
        DocumentSummaryResponse response = new DocumentSummaryResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setSubjectId(entity.getSubjectId());
        response.setCategory(entity.getCategory());
        response.setFileType(entity.getFileType());
        response.setFileSize(entity.getFileSize());
        response.setUploaderId(entity.getUploaderId());
        response.setSourceName(entity.getSourceName());
        response.setViewCount(entity.getViewCount());
        response.setDownloadCount(entity.getDownloadCount());
        response.setAverageRating(entity.getAverageRating());
        response.setRatingCount(entity.getRatingCount());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    public DocumentRatingResponse toRatingResponse(DocumentRating rating) {
        if (rating == null) {
            return null;
        }
        DocumentRatingResponse response = new DocumentRatingResponse();
        response.setId(rating.getId());
        response.setDocumentId(rating.getDocument().getId());
        response.setUserId(rating.getUserId());
        response.setScore(rating.getScore());
        response.setReview(rating.getReview());
        response.setCreatedAt(rating.getCreatedAt());
        response.setUpdatedAt(rating.getUpdatedAt());
        return response;
    }

    public AdminDocumentResponse toAdminResponse(LearningDocument entity, Long unresolvedReportCount) {
        if (entity == null) {
            return null;
        }
        AdminDocumentResponse response = new AdminDocumentResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setSubjectId(entity.getSubjectId());
        response.setCategory(entity.getCategory());
        response.setFileUrl(entity.getFileUrl());
        response.setStorageKey(entity.getStorageKey());
        response.setOriginalFileName(entity.getOriginalFileName());
        response.setFileType(entity.getFileType());
        response.setMimeType(entity.getMimeType());
        response.setFileSize(entity.getFileSize());
        response.setUploaderId(entity.getUploaderId());
        response.setSourceName(entity.getSourceName());
        response.setStatus(entity.getStatus());
        response.setRejectionReason(entity.getRejectionReason());
        response.setHiddenReason(entity.getHiddenReason());
        response.setViewCount(entity.getViewCount());
        response.setDownloadCount(entity.getDownloadCount());
        response.setAverageRating(entity.getAverageRating());
        response.setRatingCount(entity.getRatingCount());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setPublishedAt(entity.getPublishedAt());
        response.setReviewerId(entity.getReviewerId());
        response.setReviewedAt(entity.getReviewedAt());
        response.setUnresolvedReportCount(unresolvedReportCount != null ? unresolvedReportCount : 0L);
        return response;
    }
}
