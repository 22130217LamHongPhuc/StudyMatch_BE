package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.MatchingBatchStatus;
import java.time.LocalDateTime;

public record MatchingBatchSummaryResponse(
        Long id,
        Long userId,
        String algorithmType,
        MatchingBatchStatus status,
        Integer totalItems,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}

