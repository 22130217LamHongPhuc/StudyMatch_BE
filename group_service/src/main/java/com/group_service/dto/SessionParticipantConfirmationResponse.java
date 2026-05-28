package com.group_service.dto;

import com.group_service.entity.enums.StudySessionParticipantRole;
import com.group_service.entity.enums.StudySessionParticipantStatus;

import java.time.LocalDateTime;

public record SessionParticipantConfirmationResponse(
        Long userId,
        String userName,
        StudySessionParticipantRole role,
        StudySessionParticipantStatus status,
        LocalDateTime respondedAt
) {
}

