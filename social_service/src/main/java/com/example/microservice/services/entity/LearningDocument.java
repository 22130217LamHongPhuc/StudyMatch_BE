package com.example.microservice.services.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "learning_documents", indexes = {
        @Index(name = "idx_docs_status", columnList = "status"),
        @Index(name = "idx_docs_subject", columnList = "subject_id"),
        @Index(name = "idx_docs_category", columnList = "category"),
        @Index(name = "idx_docs_uploader", columnList = "uploader_id"),
        @Index(name = "idx_docs_created_at", columnList = "created_at"),
        @Index(name = "idx_docs_status_created", columnList = "status, created_at")
})
public class LearningDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private DocumentCategory category;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "storage_key", length = 255)
    private String storageKey;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    @Column(name = "source_name", length = 255)
    private String sourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DocumentStatus status = DocumentStatus.PENDING;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "hidden_reason", length = 1000)
    private String hiddenReason;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "download_count", nullable = false)
    private Long downloadCount = 0L;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @Column(name = "rating_count", nullable = false)
    private Long ratingCount = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = DocumentStatus.PENDING;
        }
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        if (this.downloadCount == null) {
            this.downloadCount = 0L;
        }
        if (this.averageRating == null) {
            this.averageRating = 0.0;
        }
        if (this.ratingCount == null) {
            this.ratingCount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
