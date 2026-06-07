package com.group_service.dto;
import com.group_service.entity.enums.StudySessionAttendanceStatus;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveStudySessionResponse {

    private Long sessionId;

    private Long userId;

    private Long attendanceLogId;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    private Long durationSeconds;

    private Long totalDurationSeconds;

    private Integer joinCount;

    private StudySessionAttendanceStatus attendanceStatus;
}