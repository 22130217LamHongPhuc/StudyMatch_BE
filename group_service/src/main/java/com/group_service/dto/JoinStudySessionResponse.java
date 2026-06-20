package com.group_service.dto;



import java.time.LocalDateTime;


public record JoinStudySessionResponse(
        Long sessionId,
        String roomId,
        String token,
        LocalDateTime joinedAt
) {
}