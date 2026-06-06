package com.example.microservice.service.impl;

import com.example.microservice.dto.admin.matching.MatchingBatchItemResponse;
import com.example.microservice.dto.admin.matching.MatchingActionResponse;
import com.example.microservice.dto.admin.matching.MatchingStatisticsResponse;
import com.example.microservice.dto.admin.matching.PageResponse;
import com.example.microservice.dto.admin.matching.StudyFeedbackResponse;
import com.example.microservice.dto.admin.matching.StudyFeedbackStatisticsResponse;
import com.example.microservice.entity.MatchingItem;
import com.example.microservice.entity.StudyFeedback;
import com.example.microservice.enums.MatchingActionStatus;
import com.example.microservice.repository.MatchingItemRepository;
import com.example.microservice.repository.StudyFeedbackRepository;
import com.example.microservice.service.AdminMatchingService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.microservice.enums.StudySessionType;

@Service
@Transactional(readOnly = true)
public class AdminMatchingServiceImpl implements AdminMatchingService {

    private final MatchingItemRepository matchingItemRepository;
    private final StudyFeedbackRepository studyFeedbackRepository;

    public AdminMatchingServiceImpl(
           MatchingItemRepository matchingItemRepository,
            StudyFeedbackRepository studyFeedbackRepository
    ) {
        this.matchingItemRepository = matchingItemRepository;
        this.studyFeedbackRepository = studyFeedbackRepository;
    }

    @Override
    public MatchingStatisticsResponse getStatistics(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = toStartOfDay(fromDate);
        LocalDateTime toDateTime = toEndExclusive(toDate);

        long totalRecommendationItems = matchingItemRepository.countFiltered(fromDateTime, toDateTime);
        long totalViewed = matchingItemRepository.countByActionStatusFiltered(MatchingActionStatus.VIEWED, fromDateTime, toDateTime);
        long totalFriendRequestSent = matchingItemRepository.countByActionStatusFiltered(MatchingActionStatus.FRIEND_REQUEST_SENT, fromDateTime, toDateTime);
        long totalRejected = matchingItemRepository.countByActionStatusFiltered(MatchingActionStatus.REJECTED, fromDateTime, toDateTime);
        long totalFeedbacks = studyFeedbackRepository.countFiltered(fromDateTime, toDateTime);
        double averageRating = safeDouble(studyFeedbackRepository.averageRatingFiltered(fromDateTime, toDateTime));
        double averageCompatibilityRating = safeDouble(studyFeedbackRepository.averageCompatibilityRatingFiltered(fromDateTime, toDateTime));

        return new MatchingStatisticsResponse(
                totalRecommendationItems,
                totalViewed,
                totalFriendRequestSent,
                totalRejected,
                totalFeedbacks,
                averageRating,
                averageCompatibilityRating
        );
    }

    @Override
    public PageResponse<MatchingActionResponse> getActions(
            int page,
            int size,
            Long userId,
            Long recommendedUserId,
            MatchingActionStatus actionStatus,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = toStartOfDay(fromDate);
        LocalDateTime toDateTime = toEndExclusive(toDate);

        Page<MatchingItem> actionPage = matchingItemRepository.findActionsPage(
                userId,
                recommendedUserId,
                actionStatus,
                fromDateTime,
                toDateTime,
                PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")))
        );

        List<MatchingActionResponse> content = actionPage.getContent().stream()
                .map(this::toActionResponse)
                .toList();

        return new PageResponse<>(
                content,
                actionPage.getNumber(),
                actionPage.getSize(),
                actionPage.getTotalElements(),
                actionPage.getTotalPages()
        );
    }


    @Override
    public PageResponse<StudyFeedbackResponse> getFeedbacks(
            int page,
            int size,
            StudySessionType sessionType,
            Long reviewerUserId,
            Long targetUserId,
            Long groupId,
            Integer minRating,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = toStartOfDay(fromDate);
        LocalDateTime toDateTime = toEndExclusive(toDate);

        Page<StudyFeedback> feedbackPage = studyFeedbackRepository.findAdminPage(
                sessionType == null ? null : sessionType.name(),
                reviewerUserId,
                targetUserId,
                groupId,
                minRating,
                fromDateTime,
                toDateTime,
                PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")))
        );

        List<StudyFeedbackResponse> content = feedbackPage.getContent().stream()
                .map(this::toFeedbackResponse)
                .toList();

        return new PageResponse<>(
                content,
                feedbackPage.getNumber(),
                feedbackPage.getSize(),
                feedbackPage.getTotalElements(),
                feedbackPage.getTotalPages()
        );
    }

    @Override
    public StudyFeedbackResponse getFeedbackDetail(Long feedbackId) {
        StudyFeedback feedback = studyFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study feedback not found"));

        return toFeedbackResponse(feedback);
    }

    @Override
    public StudyFeedbackStatisticsResponse getFeedbackStatistics(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = toStartOfDay(fromDate);
        LocalDateTime toDateTime = toEndExclusive(toDate);

        long totalFeedbacks = studyFeedbackRepository.countFiltered(fromDateTime, toDateTime);
        double averageRating = safeDouble(studyFeedbackRepository.averageRatingFiltered(fromDateTime, toDateTime));
        double averageCompatibilityRating = safeDouble(studyFeedbackRepository.averageCompatibilityRatingFiltered(fromDateTime, toDateTime));
        long oneToOneFeedbacks = studyFeedbackRepository.countBySessionTypeFiltered(StudySessionType.ONE_TO_ONE.name(), fromDateTime, toDateTime);
        long groupFeedbacks = studyFeedbackRepository.countBySessionTypeFiltered(StudySessionType.GROUP.name(), fromDateTime, toDateTime);

        Map<String, Long> ratingDistribution = new LinkedHashMap<>();
        for (int rating = 1; rating <= 5; rating++) {
            ratingDistribution.put(String.valueOf(rating), studyFeedbackRepository.countByRatingFiltered(rating, fromDateTime, toDateTime));
        }

        return new StudyFeedbackStatisticsResponse(
                totalFeedbacks,
                averageRating,
                averageCompatibilityRating,
                oneToOneFeedbacks,
                groupFeedbacks,
                ratingDistribution
        );
    }


    private MatchingBatchItemResponse toItemResponse(MatchingItem item) {
        return new MatchingBatchItemResponse(
                item.getId(),
                item.getRecommendedUserId(),
                item.getFinalScore(),
                item.getReasonText(),
                item.getActionStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    private MatchingActionResponse toActionResponse(MatchingItem item) {
        return new MatchingActionResponse(
                item.getId(),
                item.getUserId(),
                item.getRecommendedUserId(),
                item.getFinalScore(),
                item.getReasonText(),
                item.getActionStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }


    private StudyFeedbackResponse toFeedbackResponse(StudyFeedback feedback) {
        return new StudyFeedbackResponse(
                feedback.getId(),
                feedback.getSessionId(),
                feedback.getReviewerUserId(),
                toStudySessionType(feedback.getSessionType()),
                feedback.getTargetUserId(),
                feedback.getGroupId(),
                feedback.getRating(),
                feedback.getCompatibilityRating(),
                feedback.getComment(),
                feedback.getCreatedAt()
        );
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fromDate must be before or equal to toDate");
        }
    }

    private LocalDateTime toStartOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime toEndExclusive(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay();
    }

    private double safeDouble(Double value) {
        return Objects.requireNonNullElse(value, 0.0);
    }

    private StudySessionType toStudySessionType(String sessionType) {
        if (sessionType == null) {
            return null;
        }
        return StudySessionType.valueOf(sessionType);
    }
}






