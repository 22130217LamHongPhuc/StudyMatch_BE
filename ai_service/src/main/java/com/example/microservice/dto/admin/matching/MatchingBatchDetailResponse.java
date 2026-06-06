package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.MatchingBatchStatus;
import java.time.LocalDateTime;
import java.util.List;

public record MatchingBatchDetailResponse(
        Long id,
        Long userId,
        String algorithmType,
        MatchingBatchStatus status,
        Integer totalItems,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        List<MatchingBatchItemResponse> items
) {
}

