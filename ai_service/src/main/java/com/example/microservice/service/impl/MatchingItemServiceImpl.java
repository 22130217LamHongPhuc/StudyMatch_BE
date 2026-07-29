package com.example.microservice.service.impl;

import com.example.microservice.dto.CreateMatchingItemRequest;
import com.example.microservice.dto.DecidedMatchingItemsDto;
import com.example.microservice.dto.MatchingItemResponse;
import com.example.microservice.dto.UpdateMatchingItemStatusRequest;
import com.example.microservice.entity.MatchingItem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.microservice.enums.MatchingActionStatus;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.MatchingItemRepository;
import com.example.microservice.service.MatchingItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingItemServiceImpl implements MatchingItemService {

    private final MatchingItemRepository matchingItemRepository;
    private final com.example.microservice.repository.StudyFeedbackRepository studyFeedbackRepository;

    @Override
    @Transactional
    public MatchingItemResponse recordAction(CreateMatchingItemRequest request) {
        validateRequest(request.getUserId(), request.getRecommendedUserId());

        MatchingActionStatus newStatus = request.getActionStatus();
        LocalDateTime now = LocalDateTime.now();

        MatchingItem matchingItem = matchingItemRepository
                .findByUserIdAndRecommendedUserIdAndActionStatus(
                        request.getUserId(),
                        request.getRecommendedUserId(),
                        MatchingActionStatus.VIEWED)
                .map(item -> {
                    item.setCount(item.getCount() + 1);
                    return item;
                })
                .orElseGet(() -> {
                    MatchingItem item = new MatchingItem();
                    item.setUserId(request.getUserId());
                    item.setRecommendedUserId(request.getRecommendedUserId());
                    item.setFinalScore(0.0);
                    item.setIsRecommendation(true);
                    item.setActionStatus(MatchingActionStatus.VIEWED);
                    item.setCount(0);
                    return item;
                });

        updateActionTime(matchingItem, newStatus, now);

        if (shouldUpdateStatus(matchingItem.getActionStatus(), newStatus)) {
            matchingItem.setActionStatus(newStatus);
        }

        if (request.getFinalScore() != null) {
            matchingItem.setFinalScore(request.getFinalScore());
        }

        if (request.getReasonText() != null) {
            matchingItem.setReasonText(request.getReasonText());
        }

        MatchingItem savedItem = matchingItemRepository.save(matchingItem);

        return mapToResponse(savedItem);
    }

    @Override
    @Transactional
    public MatchingItemResponse recordActionSkipped(CreateMatchingItemRequest request) {
        validateRequest(request.getUserId(), request.getRecommendedUserId());

        MatchingActionStatus newStatus = request.getActionStatus();
        LocalDateTime now = LocalDateTime.now();

        MatchingItem matchingItem = matchingItemRepository
                .findByUserIdAndRecommendedUserIdAndActionStatus(
                        request.getUserId(),
                        request.getRecommendedUserId(),
                        MatchingActionStatus.SKIPPED)
                .map(item -> {
                    item.setCount(item.getCount() + 1);
                    return item;
                })
                .orElseGet(() -> {
                    MatchingItem item = new MatchingItem();
                    item.setUserId(request.getUserId());
                    item.setRecommendedUserId(request.getRecommendedUserId());
                    item.setFinalScore(0.0);
                    item.setIsRecommendation(true);
                    item.setActionStatus(MatchingActionStatus.SKIPPED);
                    item.setCount(0);
                    return item;
                });

        updateActionTime(matchingItem, newStatus, now);

        if (shouldUpdateStatus(matchingItem.getActionStatus(), newStatus)) {
            matchingItem.setActionStatus(newStatus);
        }

        if (request.getFinalScore() != null) {
            matchingItem.setFinalScore(request.getFinalScore());
        }

        if (request.getReasonText() != null) {
            matchingItem.setReasonText(request.getReasonText());
        }

        MatchingItem savedItem = matchingItemRepository.save(matchingItem);

        return mapToResponse(savedItem);
    }

    @Override
    @Transactional
    public MatchingItemResponse recordFriendRequest(CreateMatchingItemRequest request) {
        MatchingItem matchingItem = new MatchingItem();
        matchingItem.setUserId(request.getUserId());
        matchingItem.setRecommendedUserId(request.getRecommendedUserId());
        matchingItem.setFinalScore(0.0);
        matchingItem.setIsRecommendation(true);
        LocalDateTime now = LocalDateTime.now();

        updateActionTime(matchingItem, MatchingActionStatus.FRIEND_REQUEST_SENT, now);

        if (shouldUpdateStatus(matchingItem.getActionStatus(), MatchingActionStatus.FRIEND_REQUEST_SENT)) {
            matchingItem.setActionStatus(MatchingActionStatus.FRIEND_REQUEST_SENT);
        }

        if (request.getFinalScore() != null) {
            matchingItem.setFinalScore(request.getFinalScore());
        }

        if (request.getReasonText() != null) {
            matchingItem.setReasonText(request.getReasonText());
        }

        MatchingItem savedItem = matchingItemRepository.save(matchingItem);
        return mapToResponse(savedItem);
    }

    @Override
    @Transactional
    public MatchingItemResponse updateMatchingItemStatus(UpdateMatchingItemStatusRequest request) {
        validateRequest(request.getUserId(), request.getRecommendedUserId());

        MatchingActionStatus actionStatus = request.getActionStatus();
        MatchingItem matchingItem = matchingItemRepository
                .findBidirectionalByActionStatus(request.getUserId(),
                        request.getRecommendedUserId(),
                        MatchingActionStatus.FRIEND_REQUEST_SENT)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException("Matching item not found"));

        LocalDateTime now = LocalDateTime.now();

        updateActionTime(matchingItem, actionStatus, now);

        if (shouldUpdateStatus(matchingItem.getActionStatus(), actionStatus)) {
            matchingItem.setActionStatus(actionStatus);
        }

        MatchingItem saved = matchingItemRepository.save(matchingItem);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MatchingItemResponse recordActionCancelled(CreateMatchingItemRequest request) {
        validateRequest(request.getUserId(), request.getRecommendedUserId());

        MatchingItem matchingItem = matchingItemRepository
                .findBidirectionalByActionStatus(request.getUserId(),
                        request.getRecommendedUserId(),
                        MatchingActionStatus.ACCEPTED)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException("Matching item not found"));

        LocalDateTime now = LocalDateTime.now();

        updateActionTime(matchingItem, MatchingActionStatus.CANCELLED, now);

        if (shouldUpdateStatus(matchingItem.getActionStatus(), MatchingActionStatus.CANCELLED)) {
            matchingItem.setActionStatus(MatchingActionStatus.CANCELLED);
        }

        MatchingItem saved = matchingItemRepository.save(matchingItem);

        return mapToResponse(saved);
    }

    private void updateActionTime(
            MatchingItem matchingItem,
            MatchingActionStatus status,
            LocalDateTime now) {
        if (status == MatchingActionStatus.VIEWED) {
            matchingItem.setViewedAt(now);
        } else if (status == MatchingActionStatus.FRIEND_REQUEST_SENT) {
            matchingItem.setRequestSentAt(now);
        } else if (status == MatchingActionStatus.ACCEPTED ||
                status == MatchingActionStatus.REJECTED) {
            matchingItem.setRespondedAt(now);
        }
    }

    private boolean shouldUpdateStatus(
            MatchingActionStatus currentStatus,
            MatchingActionStatus newStatus) {
        if (currentStatus == null) {
            return true;
        }

        if (newStatus == MatchingActionStatus.CANCELLED && currentStatus == MatchingActionStatus.ACCEPTED) {
            return true;
        }

        if (isFinalStatus(currentStatus)) {
            return false;
        }

        if (newStatus == MatchingActionStatus.CANCELLED) {
            return currentStatus == MatchingActionStatus.FRIEND_REQUEST_SENT;
        }

        if (currentStatus == MatchingActionStatus.CANCELLED) {
            return newStatus == MatchingActionStatus.FRIEND_REQUEST_SENT || newStatus == MatchingActionStatus.SKIPPED;
        }

        return getStatusPriority(newStatus) > getStatusPriority(currentStatus);
    }

    private boolean isFinalStatus(MatchingActionStatus status) {
        return status == MatchingActionStatus.ACCEPTED ||
                status == MatchingActionStatus.REJECTED;
    }

    private int getStatusPriority(MatchingActionStatus status) {
        return switch (status) {
            case VIEWED -> 1;
            case FRIEND_REQUEST_SENT -> 2;
            case REJECTED -> 3;
            case ACCEPTED -> 3;
            case SKIPPED -> 4;
            case CANCELLED -> 0;
        };
    }

    private void validateRequest(Long userId, Long recommendedUserId) {
        if (userId.equals(recommendedUserId)) {
            throw new AppException(
                    "User cannot match with themselves",
                    StatusCode.BAD_REQUEST);
        }
    }

    private MatchingItemResponse mapToResponse(MatchingItem matchingItem) {
        return MatchingItemResponse.builder()
                .id(matchingItem.getId())
                .userId(matchingItem.getUserId())
                .recommendedUserId(matchingItem.getRecommendedUserId())
                .finalScore(matchingItem.getFinalScore())
                .reasonText(matchingItem.getReasonText())
                .count(matchingItem.getCount())
                .actionStatus(matchingItem.getActionStatus())
                .viewedAt(matchingItem.getViewedAt())
                .requestSentAt(matchingItem.getRequestSentAt())
                .respondedAt(matchingItem.getRespondedAt())
                .createdAt(matchingItem.getCreatedAt())
                .updatedAt(matchingItem.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DecidedMatchingItemsDto getDecidedMatchingItems(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        List<MatchingActionStatus> acceptedStatuses = List.of(
                MatchingActionStatus.ACCEPTED,
                MatchingActionStatus.FRIEND_REQUEST_SENT);

        List<MatchingItemResponse> accepted = matchingItemRepository
                .findRelatedByUserIdAndActionStatusInOrderByUpdatedAtDesc(
                        userId,
                        acceptedStatuses,
                        pageable)
                .stream()
                .map(this::mapToResponse)
                .toList();

        List<MatchingItemResponse> rejected = matchingItemRepository
                .findRelatedByUserIdAndActionStatusOrderByUpdatedAtDesc(
                        userId,
                        MatchingActionStatus.REJECTED,
                        pageable)
                .stream()
                .map(this::mapToResponse)
                .toList();

        List<MatchingItemResponse> cancelled = matchingItemRepository
                .findRelatedByUserIdAndActionStatusOrderByUpdatedAtDesc(
                        userId,
                        MatchingActionStatus.CANCELLED,
                        pageable)
                .stream()
                .map(this::mapToResponse)
                .toList();

        List<MatchingItemResponse> skipped = matchingItemRepository
                .findRelatedByUserIdAndActionStatusOrderByUpdatedAtDesc(
                        userId,
                        MatchingActionStatus.SKIPPED,
                        pageable)
                .stream()
                .map(this::mapToResponse)
                .toList();

        return DecidedMatchingItemsDto.builder()
                .accepted(accepted)
                .rejected(rejected)
                .skipped(skipped)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, java.util.List<Long>> getUserFeedbackPreferences(Long userId) {
        java.util.List<Long> positiveUids = new java.util.ArrayList<>();
        java.util.List<Long> negativeUids = new java.util.ArrayList<>();

        // 1. Lấy actions tích cực (ACCEPTED, FRIEND_REQUEST_SENT)
        java.util.List<com.example.microservice.enums.MatchingActionStatus> positiveStatuses = java.util.List.of(
                com.example.microservice.enums.MatchingActionStatus.ACCEPTED,
                com.example.microservice.enums.MatchingActionStatus.FRIEND_REQUEST_SENT
        );
        java.util.List<MatchingItem> positiveItems = matchingItemRepository
                .findRelatedByUserIdAndActionStatusInOrderByUpdatedAtDesc(userId, positiveStatuses, org.springframework.data.domain.Pageable.unpaged());
        for (MatchingItem item : positiveItems) {
            if (item.getUserId().equals(userId)) {
                positiveUids.add(item.getRecommendedUserId());
            } else {
                positiveUids.add(item.getUserId());
            }
        }

        // 2. Lấy actions tiêu cực (REJECTED, SKIPPED)
        java.util.List<MatchingItem> rejectedItems = matchingItemRepository
                .findRelatedByUserIdAndActionStatusOrderByUpdatedAtDesc(userId, com.example.microservice.enums.MatchingActionStatus.REJECTED, org.springframework.data.domain.Pageable.unpaged());
        for (MatchingItem item : rejectedItems) {
            if (item.getUserId().equals(userId)) {
                negativeUids.add(item.getRecommendedUserId());
            } else {
                negativeUids.add(item.getUserId());
            }
        }

        java.util.List<MatchingItem> skippedItems = matchingItemRepository
                .findRelatedByUserIdAndActionStatusOrderByUpdatedAtDesc(userId, com.example.microservice.enums.MatchingActionStatus.SKIPPED, org.springframework.data.domain.Pageable.unpaged());
        for (MatchingItem item : skippedItems) {
            if (item.getUserId().equals(userId)) {
                negativeUids.add(item.getRecommendedUserId());
            } else {
                negativeUids.add(item.getUserId());
            }
        }

        // 3. Lấy feedbacks (USER_PAIR)
        java.util.List<com.example.microservice.entity.StudyFeedback> feedbacks = studyFeedbackRepository
                .findByReviewerUserIdAndSessionTypeAndTargetUserIdIsNotNull(userId, com.example.microservice.enums.StudySessionType.USER_PAIR);
        for (com.example.microservice.entity.StudyFeedback fb : feedbacks) {
            if (fb.getRating() != null) {
                if (fb.getRating() >= 4) {
                    positiveUids.add(fb.getTargetUserId());
                } else if (fb.getRating() <= 2) {
                    negativeUids.add(fb.getTargetUserId());
                }
            }
        }

        // Loại bỏ trùng lặp và bản thân user
        java.util.List<Long> uniquePos = positiveUids.stream().filter(id -> !id.equals(userId)).distinct().toList();
        java.util.List<Long> uniqueNeg = negativeUids.stream().filter(id -> !id.equals(userId)).distinct().toList();

        java.util.Map<String, java.util.List<Long>> result = new java.util.HashMap<>();
        result.put("positiveUserIds", uniquePos);
        result.put("negativeUserIds", uniqueNeg);

        return result;
    }
}