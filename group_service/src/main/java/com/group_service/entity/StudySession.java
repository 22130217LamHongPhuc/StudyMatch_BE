package com.group_service.entity;

import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, length = 150)
    private String title;

    @jakarta.persistence.Lob
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_name")
    private String subjectName;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_mode", nullable = false, length = 20)
    private GroupStudySessionMode studyMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private StudySessionType sessionType;

    private String location;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupStudySessionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reminder_sent")
    private boolean reminderSent = false;
}


