package com.example.microservice.entity;

import com.example.microservice.enums.StudyFeedbackType;
import com.example.microservice.enums.StudySessionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "study_feedbacks",
        indexes = {
                @Index(name = "idx_study_feedbacks_session_id", columnList = "session_id"),
                @Index(name = "idx_study_feedbacks_reviewer_user_id", columnList = "reviewer_user_id"),
                @Index(name = "idx_study_feedbacks_target_user_id", columnList = "target_user_id"),
                @Index(name = "idx_study_feedbacks_group_id", columnList = "group_id"),
                @Index(name = "idx_study_feedbacks_type", columnList = "feedback_type"),
                @Index(name = "idx_study_feedbacks_model", columnList = "eligible_for_model")
        }
)
public class StudyFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "reviewer_user_id", nullable = false)
    private Long reviewerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 30)
    private StudySessionType sessionType;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "group_id")
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 30)
    private StudyFeedbackType feedbackType;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "matched_quality_score")
    private Integer matchedQualityScore;

    @Column(name = "communication_score")
    private Integer communicationScore;

    @Column(name = "study_effectiveness_score")
    private Integer studyEffectivenessScore;

    @Column(name = "eligible_for_model")
    private Boolean eligibleForModel = false;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.eligibleForModel == null) {
            this.eligibleForModel = false;
        }
    }
}