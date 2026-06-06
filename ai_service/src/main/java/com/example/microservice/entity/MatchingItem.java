package com.example.microservice.entity;

import com.example.microservice.enums.MatchingActionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "matching_items",
        indexes = {
                @Index(name = "idx_matching_items_batch_id", columnList = "batch_id"),
                @Index(name = "idx_matching_items_user_id", columnList = "user_id"),
                @Index(name = "idx_matching_items_recommended_user_id", columnList = "recommended_user_id"),
                @Index(name = "idx_matching_items_action_status", columnList = "action_status")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_batch_recommended_user",
                        columnNames = {"batch_id", "recommended_user_id"}
                )
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
    private MatchingActionStatus actionStatus = MatchingActionStatus.NONE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
        if (this.actionStatus == null) {
            this.actionStatus = MatchingActionStatus.NONE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}