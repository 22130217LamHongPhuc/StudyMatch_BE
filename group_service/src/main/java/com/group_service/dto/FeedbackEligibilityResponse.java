package com.group_service.dto;
import com.group_service.entity.enums.StudyFeedbackType;
import com.group_service.entity.enums.StudySessionAttendanceStatus;
import com.group_service.entity.enums.StudySessionType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackEligibilityResponse {

    private Long sessionId;

    private Long userId;

    private StudySessionType sessionType;

    private Long targetUserId;

    private Long groupId;

    private Boolean sessionEnded;

    private Boolean canSubmitFeedback;

    private StudyFeedbackType feedbackType;

    private Long totalDurationSeconds;

    private Long minRequiredDurationSeconds;

    private StudySessionAttendanceStatus attendanceStatus;

    private Boolean eligibleForModel;

    private String message;
}