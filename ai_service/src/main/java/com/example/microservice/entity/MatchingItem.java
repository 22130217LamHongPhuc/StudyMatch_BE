package com.example.microservice.entity;

import com.example.microservice.enums.MatchingActionStatus;
import com.example.microservice.enums.MatchingRejectType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "matching_items", indexes = {
        @Index(name = "idx_matching_items_user_id", columnList = "user_id"),
        @Index(name = "idx_matching_items_recommended_user_id", columnList = "recommended_user_id"),
        @Index(name = "idx_matching_items_action_status", columnList = "action_status")
}

)
public class MatchingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recommended_user_id", nullable = false)
    private Long recommendedUserId;

    @Column(name = "final_score")
    private Double finalScore = 0.0;

    @Column(name = "reason_text", columnDefinition = "TEXT")
    private String reasonText;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_status", nullable = false)
    private MatchingActionStatus actionStatus = MatchingActionStatus.VIEWED;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_type")
    private MatchingRejectType rejectType;

    @Column(name = "is_recommendation", nullable = false, columnDefinition = "boolean default false")
    private Boolean isRecommendation = false;

    @Column(name = "count", nullable = false, columnDefinition = "int default 0")
    private Integer count = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "request_sent_at")
    private LocalDateTime requestSentAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
        if (this.finalScore == null) {
            this.finalScore = 0.0;
        }
        if (this.count == null) {
            this.count = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}