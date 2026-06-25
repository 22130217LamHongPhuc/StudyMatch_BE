package com.example.microservice.dto.admin.matching;

import java.time.LocalDate;

public record MatchingTrendResponse(
        LocalDate date,
        long totalRecommendations,
        long totalViewed,
        long totalFriendRequestSent,
        long totalAccepted,
        long totalRejected
) {
}
