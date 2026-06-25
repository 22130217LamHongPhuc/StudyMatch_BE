package com.example.microservice.service.impl;

import com.example.microservice.dto.BasicUserResponse;
import com.example.microservice.dto.admin.matching.*;
import com.example.microservice.entity.MatchingItem;
import com.example.microservice.entity.StudyFeedback;
import com.example.microservice.enums.MatchingActionStatus;
import com.example.microservice.fetchClient.UserClient;
import com.example.microservice.repository.MatchingItemRepository;
import com.example.microservice.repository.StudyFeedbackRepository;
import com.example.microservice.service.AdminMatchingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final UserClient userClient;
    private final MatchingItemRepository matchingItemRepository;
    private final StudyFeedbackRepository studyFeedbackRepository;

    public AdminMatchingServiceImpl(
            UserClient userClient, MatchingItemRepository matchingItemRepository,
            StudyFeedbackRepository studyFeedbackRepository
    ) {
        this.userClient = userClient;
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
        long totalAccepted = matchingItemRepository.countByActionStatusFiltered(MatchingActionStatus.ACCEPTED, fromDateTime, toDateTime);

        double viewRate = 0.0;
        double friendRequestRate = 0.0;
        double acceptRate = 0.0;
        double rejectRate = 0.0;

        if (totalRecommendationItems > 0) {
            viewRate = round((double) totalViewed / totalRecommendationItems);
            friendRequestRate = round((double) totalFriendRequestSent / totalRecommendationItems);
            acceptRate = round((double) totalAccepted / totalRecommendationItems);
            rejectRate = round((double) totalRejected / totalRecommendationItems);
        }

        double averageFinalScore = round(safeDouble(matchingItemRepository.averageFinalScoreFiltered(fromDateTime, toDateTime)));
        long totalFeedbacks = studyFeedbackRepository.countFiltered(fromDateTime, toDateTime);
        double averageRating = safeDouble(studyFeedbackRepository.averageRatingFiltered(fromDateTime, toDateTime));

        return new MatchingStatisticsResponse(
                totalRecommendationItems,
                totalViewed,
                totalFriendRequestSent,
                totalRejected,
                totalAccepted,
                viewRate,
                friendRequestRate,
                acceptRate,
                rejectRate,
                averageFinalScore,
                totalFeedbacks,
                averageRating
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

        List<MatchingItem> items = actionPage.getContent();

        List<Long> userIds = items.stream()
                .flatMap(item -> Stream.of(item.getUserId(), item.getRecommendedUserId()))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, BasicUserResponse> userMap = getUserMap(userIds);

        List<MatchingActionResponse> content = items.stream()
                .map(item -> toActionResponse(item, userMap))
                .toList();

        return new PageResponse<>(
                content,
                actionPage.getNumber(),
                actionPage.getSize(),
                actionPage.getTotalElements(),
                actionPage.getTotalPages()
        );
    }


    private Map<Long, BasicUserResponse> getUserMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            ApiResponse<List<BasicUserResponse>> response = userClient.getBasicUsers(userIds);

            if (response == null || !response.isSuccess() || response.getData() == null) {
                return Collections.emptyMap();
            }

            return response.getData().stream()
                    .filter(user -> user.getUserId() != null)
                    .collect(Collectors.toMap(
                            BasicUserResponse::getUserId,
                            Function.identity(),
                            (oldValue, newValue) -> oldValue
                    ));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
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
                sessionType,
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
        long oneToOneFeedbacks = studyFeedbackRepository.countBySessionTypeFiltered(StudySessionType.USER_PAIR, fromDateTime, toDateTime);
        long groupFeedbacks = studyFeedbackRepository.countBySessionTypeFiltered(StudySessionType.GROUP, fromDateTime, toDateTime);

        Map<String, Long> ratingDistribution = new LinkedHashMap<>();
        for (int rating = 1; rating <= 5; rating++) {
            ratingDistribution.put(String.valueOf(rating), studyFeedbackRepository.countByRatingFiltered(rating, fromDateTime, toDateTime));
        }

        return new StudyFeedbackStatisticsResponse(
                totalFeedbacks,
                averageRating,
                oneToOneFeedbacks,
                0,
                groupFeedbacks,
                ratingDistribution
        );
    }



    private MatchingActionResponse toActionResponse(
            MatchingItem item,
            Map<Long, BasicUserResponse> userMap
    ) {
        BasicUserResponse user = userMap.get(item.getUserId());
        BasicUserResponse recommendedUser = userMap.get(item.getRecommendedUserId());

        return MatchingActionResponse.builder()
                .id(item.getId())

                .userId(item.getUserId())
                .userFullName(user != null ? user.getFullName() : null)
                .userAvatarUrl(user != null ? user.getAvatarUrl() : null)
                .userEmail(user != null ? user.getEmail() : null)

                .recommendedUserId(item.getRecommendedUserId())
                .recommendedUserFullName(recommendedUser != null ? recommendedUser.getFullName() : null)
                .recommendedUserAvatarUrl(recommendedUser != null ? recommendedUser.getAvatarUrl() : null)
                .recommendedUserEmail(recommendedUser != null ? recommendedUser.getEmail() : null)

                .actionStatus(item.getActionStatus())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }


    private StudyFeedbackResponse toFeedbackResponse(StudyFeedback feedback) {
        return StudyFeedbackResponse.builder()
                .id(feedback.getId())
                .sessionId(feedback.getSessionId())
                .reviewerUserId(feedback.getReviewerUserId())
                .targetUserId(feedback.getTargetUserId())
                .groupId(feedback.getGroupId())
                .sessionType(feedback.getSessionType())
                .feedbackType(feedback.getFeedbackType())
                .rating(feedback.getRating())
                .matchedQualityScore(feedback.getMatchedQualityScore())
                .communicationScore(feedback.getCommunicationScore())
                .studyEffectivenessScore(feedback.getStudyEffectivenessScore())
                .eligibleForModel(feedback.getEligibleForModel())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .build();
    }

    @Override
    public Map<String, Long> getActionDistribution(LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);
        LocalDateTime fromDateTime = toStartOfDay(fromDate);
        LocalDateTime toDateTime = toEndExclusive(toDate);

        Map<String, Long> distribution = new LinkedHashMap<>();
        for (MatchingActionStatus status : MatchingActionStatus.values()) {
            long count = matchingItemRepository.countByActionStatusFiltered(status, fromDateTime, toDateTime);
            distribution.put(status.name(), count);
        }
        return distribution;
    }

    @Override
    public List<MatchingTrendResponse> getTrend(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null) {
            fromDate = LocalDate.now().minusDays(7);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = toStartOfDay(fromDate);
        LocalDateTime toDateTime = toEndExclusive(toDate);

        List<Object[]> results = matchingItemRepository.findTrendData(fromDateTime, toDateTime);
        Map<LocalDate, Map<MatchingActionStatus, Long>> countsByDateAndStatus = new HashMap<>();

        for (Object[] row : results) {
            LocalDateTime createdAt = (LocalDateTime) row[0];
            MatchingActionStatus status = (MatchingActionStatus) row[1];
            if (createdAt != null) {
                LocalDate date = createdAt.toLocalDate();
                countsByDateAndStatus
                        .computeIfAbsent(date, d -> new EnumMap<>(MatchingActionStatus.class))
                        .merge(status, 1L, Long::sum);
            }
        }

        List<MatchingTrendResponse> trend = new ArrayList<>();
        LocalDate current = fromDate;
        while (!current.isAfter(toDate)) {
            Map<MatchingActionStatus, Long> statusCounts = countsByDateAndStatus.getOrDefault(current, Map.of());

            long totalViewed = statusCounts.getOrDefault(MatchingActionStatus.VIEWED, 0L);
            long totalFriendRequestSent = statusCounts.getOrDefault(MatchingActionStatus.FRIEND_REQUEST_SENT, 0L);
            long totalAccepted = statusCounts.getOrDefault(MatchingActionStatus.ACCEPTED, 0L);
            long totalRejected = statusCounts.getOrDefault(MatchingActionStatus.REJECTED, 0L);

            long totalRecommendations = totalViewed + totalFriendRequestSent + totalAccepted + totalRejected;

            trend.add(new MatchingTrendResponse(
                    current,
                    totalRecommendations,
                    totalViewed,
                    totalFriendRequestSent,
                    totalAccepted,
                    totalRejected
            ));
            current = current.plusDays(1);
        }

        return trend;
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

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}






