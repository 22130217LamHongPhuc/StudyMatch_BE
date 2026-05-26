package com.group_service.dto;

import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;

import java.time.LocalDateTime;

public record StudySessionResponse(
        Long id,
        Long groupId,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        GroupStudySessionMode studyMode,
        String location,
        Long createdByUserId,
        GroupStudySessionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String subjectName
) {
}

