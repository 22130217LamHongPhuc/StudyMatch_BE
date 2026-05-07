package com.group_service.dto;

import java.time.LocalDateTime;

public record StudyGroupResponse(
        Long id,
        String name,
        String description,
        Long ownerUserId,
        Long termId,
        Byte studyYearNo,
        Byte semesterNo,
        Long mainSubjectId,
        String subjectName,
        String studyGoal,
        String studyMode,
        Integer maxMembers,
        String visibility,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

