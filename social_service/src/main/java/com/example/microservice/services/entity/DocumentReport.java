package com.example.microservice.services.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "document_reports",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_document_report_reporter", columnNames = {"document_id", "reporter_id"})
    },
    indexes = {
        @Index(name = "idx_reports_status", columnList = "status"),
        @Index(name = "idx_reports_reporter", columnList = "reporter_id")
    }
)
public class DocumentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private LearningDocument document;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private DocumentReportReason reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DocumentReportStatus status = DocumentReportStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = DocumentReportStatus.PENDING;
        }
    }
}
