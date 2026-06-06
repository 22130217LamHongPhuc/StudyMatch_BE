package com.example.microservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "study_feedbacks", indexes = {
        @Index(name = "idx_study_feedbacks_session_id", columnList = "session_id"),
        @Index(name = "idx_study_feedbacks_reviewer_user_id", columnList = "reviewer_user_id"),
        @Index(name = "idx_study_feedbacks_target_user_id", columnList = "target_user_id"),
        @Index(name = "idx_study_feedbacks_group_id", columnList = "group_id")
})
public class StudyFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "reviewer_user_id", nullable = false)
    private Long reviewerUserId;

    @Column(name = "session_type", nullable = false, length = 30)
    private String sessionType;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "compatibility_rating")
    private Integer compatibilityRating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}