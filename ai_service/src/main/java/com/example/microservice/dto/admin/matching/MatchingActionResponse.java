package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.MatchingActionStatus;
import java.time.LocalDateTime;

public record MatchingActionResponse(
        Long id,
        Long userId,
        Long recommendedUserId,
        Double finalScore,
        String reasonText,
        MatchingActionStatus actionStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
