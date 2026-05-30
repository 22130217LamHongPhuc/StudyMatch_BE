package com.group_service.dto;

import com.group_service.entity.enums.StudySessionType;

import java.util.List;

public record SessionConfirmationStatsResponse(
        Long sessionId,
        StudySessionType sessionType,
        Long currentUserId,
        long totalParticipants,
        long acceptedCount,
        long pendingCount,
        long declinedCount,
        List<SessionParticipantConfirmationResponse> otherParticipants
) {
}

