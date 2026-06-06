package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.StudySessionType;
import java.time.LocalDateTime;

public record StudyFeedbackResponse(
        Long id,
        Long sessionId,
        Long reviewerUserId,
        StudySessionType sessionType,
        Long targetUserId,
        Long groupId,
        Integer rating,
        Integer compatibilityRating,
        String comment,
        LocalDateTime createdAt
) {
}

