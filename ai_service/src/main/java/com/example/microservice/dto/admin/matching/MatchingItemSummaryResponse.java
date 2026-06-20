package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.MatchingActionStatus;
import java.time.LocalDateTime;

public record MatchingItemSummaryResponse(
        Long id,
        Long batchId,
        Long userId,
        Long recommendedUserId,
        Double finalScore,
        String reasonText,
        MatchingActionStatus actionStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

