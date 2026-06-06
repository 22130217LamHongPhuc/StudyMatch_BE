package com.example.microservice.dto.admin.matching;

public record MatchingStatisticsResponse(
        long totalRecommendationItems,
        long totalViewed,
        long totalFriendRequestSent,
        long totalRejected,
        long totalFeedbacks,
        double averageRating,
        double averageCompatibilityRating
) {
}

