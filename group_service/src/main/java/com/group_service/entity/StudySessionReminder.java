package com.group_service.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_session_reminders",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_session_user_reminder",
                columnNames = {
                        "session_id",
                        "user_id",
                        "remind_before_minutes"
                }
        ),
        indexes = {
                @Index(name = "idx_reminder_session", columnList = "session_id"),
                @Index(name = "idx_reminder_user", columnList = "user_id"),
                @Index(name = "idx_reminder_sent", columnList = "is_sent")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "remind_before_minutes", nullable = false)
    private Integer remindBeforeMinutes;

    @Column(name = "is_sent", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isSent;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private StudySession studySession;
}