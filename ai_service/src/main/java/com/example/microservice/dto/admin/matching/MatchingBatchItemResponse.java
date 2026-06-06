package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.MatchingActionStatus;
import java.time.LocalDateTime;

public record MatchingBatchItemResponse(
        Long id,
        Long recommendedUserId,
        Double finalScore,
        String reasonText,
        MatchingActionStatus actionStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

