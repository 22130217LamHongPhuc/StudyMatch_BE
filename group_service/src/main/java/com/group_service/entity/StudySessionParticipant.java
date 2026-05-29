package com.group_service.entity;

import com.group_service.entity.enums.StudySessionParticipantRole;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_session_participants",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_session_user",
                columnNames = {"session_id", "user_id"}
        ),
        indexes = {
                @Index(name = "idx_participant_session", columnList = "session_id"),
                @Index(name = "idx_participant_user", columnList = "user_id"),
                @Index(name = "idx_participant_status", columnList = "status"),
                @Index(name = "idx_participant_role", columnList = "role")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudySessionParticipantRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudySessionParticipantStatus status;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private StudySession studySession;
}