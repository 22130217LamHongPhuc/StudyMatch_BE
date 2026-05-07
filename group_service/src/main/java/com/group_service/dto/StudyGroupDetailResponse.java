package com.group_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StudyGroupDetailResponse(
        Long id,
        String name,
        String description,
        Long ownerUserId,
        Long termId,
        Long mainSubjectId,
        String subjectName,
        String studyGoal,
        String studyMode,
        Integer maxMembers,
        String visibility,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<FreeTimeSlotResponse> freeTimeSlots
) {
}

