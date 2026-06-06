package com.example.microservice.dto.admin.matching;

import java.util.Map;

public record StudyFeedbackStatisticsResponse(
        long totalFeedbacks,
        double averageRating,
        double averageCompatibilityRating,
        long oneToOneFeedbacks,
        long groupFeedbacks,
        Map<String, Long> ratingDistribution
) {
}
