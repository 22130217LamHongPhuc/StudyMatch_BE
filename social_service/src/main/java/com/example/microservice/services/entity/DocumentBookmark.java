package com.example.microservice.services.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "document_bookmarks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_document_bookmark_user", columnNames = {"document_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_bookmarks_user", columnList = "user_id")
    }
)
public class DocumentBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private LearningDocument document;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
