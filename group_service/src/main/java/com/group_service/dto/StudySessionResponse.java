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
        String roomId,
        Long createdByUserId,
        GroupStudySessionStatus status,
        StudySessionParticipantStatus participantStatus,
        String partnerName,
        String partnerUserName,
        String groupName,
        String groupAvatarUrl,
        Long membersCount,
        String subjectName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer totalCreated,
        String recurrenceId,
        String recurrenceType
) {
}
