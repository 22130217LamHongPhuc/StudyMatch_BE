package com.group_service.dto;

import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import com.group_service.entity.enums.StudySessionType;

import java.time.LocalDateTime;

public record StudySessionResponse(
        Long id,
        StudySessionType sessionType,
        Long groupId,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        GroupStudySessionMode studyMode,
        String location,
        String meetingUrl,
        Long createdByUserId,
        GroupStudySessionStatus status,
        StudySessionParticipantStatus participantStatus,
        String partnerName,
        String groupName,
        Long membersCount,
        String subjectName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
