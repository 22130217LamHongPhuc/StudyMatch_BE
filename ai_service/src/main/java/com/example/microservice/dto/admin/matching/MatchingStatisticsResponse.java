package com.example.microservice.dto.admin.matching;

public record MatchingStatisticsResponse(
        long totalRecommendationItems,
        long totalViewed,
        long totalFriendRequestSent,
        long totalRejected,
        long totalAccepted,
        long totalSkipped,
        double viewRate,
        double friendRequestRate,
        double acceptRate,
        double rejectRate,
        double skipRate,
        double averageFinalScore,
        long totalFeedbacks,
        double averageRating
) {
}


