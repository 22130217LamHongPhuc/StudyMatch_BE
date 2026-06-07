package com.example.microservice.service;

import com.example.microservice.dto.admin.matching.MatchingActionResponse;
import com.example.microservice.dto.admin.matching.MatchingStatisticsResponse;
import com.example.microservice.dto.admin.matching.PageResponse;
import com.example.microservice.dto.admin.matching.StudyFeedbackResponse;
import com.example.microservice.dto.admin.matching.StudyFeedbackStatisticsResponse;
import com.example.microservice.enums.MatchingActionStatus;
import com.example.microservice.enums.StudySessionType;
import java.time.LocalDate;

public interface AdminMatchingService {

    MatchingStatisticsResponse getStatistics(LocalDate fromDate, LocalDate toDate);

    PageResponse<MatchingActionResponse> getActions(
            int page,
            int size,
            Long userId,
            Long recommendedUserId,
            MatchingActionStatus actionStatus,
            LocalDate fromDate,
            LocalDate toDate
    );

    PageResponse<StudyFeedbackResponse> getFeedbacks(
            int page,
            int size,
            StudySessionType sessionType,
            Long reviewerUserId,
            Long targetUserId,
            Long groupId,
            Integer minRating,
            LocalDate fromDate,
            LocalDate toDate
    );

    StudyFeedbackResponse getFeedbackDetail(Long feedbackId);

    StudyFeedbackStatisticsResponse getFeedbackStatistics(LocalDate fromDate, LocalDate toDate);
}

