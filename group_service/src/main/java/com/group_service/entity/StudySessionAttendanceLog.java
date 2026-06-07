package com.group_service.entity;
import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionLeaveReason;
import com.group_service.entity.enums.StudySessionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_session_attendance_logs",
        indexes = {
                @Index(name = "idx_attendance_session", columnList = "session_id"),
                @Index(name = "idx_attendance_user", columnList = "user_id"),
                @Index(name = "idx_attendance_participant", columnList = "participant_id"),
                @Index(name = "idx_attendance_open_log", columnList = "session_id,user_id,left_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionAttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_reason", length = 30)
    private StudySessionLeaveReason leaveReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private StudySession studySession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", insertable = false, updatable = false)
    private StudySessionParticipant participant;
}