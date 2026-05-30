package com.group_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_session_feedbacks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_session_reviewer_target",
                columnNames = {
                        "session_id",
                        "reviewer_user_id",
                        "target_user_id"
                }
        ),
        indexes = {
                @Index(name = "idx_feedback_session", columnList = "session_id"),
                @Index(name = "idx_feedback_reviewer", columnList = "reviewer_user_id"),
                @Index(name = "idx_feedback_target_user", columnList = "target_user_id"),
                @Index(name = "idx_feedback_group", columnList = "group_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "reviewer_user_id", nullable = false)
    private Long reviewerUserId;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Byte rating;

    @Column(name = "compatibility_score", columnDefinition = "TINYINT")
    private Byte compatibilityScore;

    @Column(length = 500)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private StudySession studySession;
}