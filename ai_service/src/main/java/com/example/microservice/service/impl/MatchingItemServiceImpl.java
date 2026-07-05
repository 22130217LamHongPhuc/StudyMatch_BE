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

    @Override
    @Transactional
    public MatchingItemResponse recordAction(CreateMatchingItemRequest request) {
        validateRequest(request.getUserId(), request.getRecommendedUserId());

        MatchingActionStatus actionStatus = request.getActionStatus();

        if (actionStatus == MatchingActionStatus.ACCEPTED ||
                actionStatus == MatchingActionStatus.REJECTED) {

            MatchingItem firstSide = upsertOneSide(
                    request.getUserId(),
                    request.getRecommendedUserId(),
                    actionStatus,
                    request.getFinalScore(),
                    request.getReasonText(),
                    false,
                    false);

            upsertOneSide(
                    request.getRecommendedUserId(),
                    request.getUserId(),
                    actionStatus,
                    request.getFinalScore(),
                    request.getReasonText(),
                    false,
                    false);

            return mapToResponse(firstSide);
        }

        MatchingItem saved = upsertOneSide(
                request.getUserId(),
                request.getRecommendedUserId(),
                actionStatus,
                request.getFinalScore(),
                request.getReasonText(),
                true,
                request.getIsRecommendation()

        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MatchingItemResponse updateMatchingItemStatus(UpdateMatchingItemStatusRequest request) {
        validateRequest(request.getUserId(), request.getRecommendedUserId());

        MatchingActionStatus actionStatus = request.getActionStatus();

        if (actionStatus == MatchingActionStatus.ACCEPTED ||
                actionStatus == MatchingActionStatus.REJECTED) {

            MatchingItem firstSide = upsertOneSide(
                    request.getUserId(),
                    request.getRecommendedUserId(),
                    actionStatus,
                    request.getFinalScore(),
                    request.getReasonText(),
                    false,
                    false);

            upsertOneSide(
                    request.getRecommendedUserId(),
                    request.getUserId(),
                    actionStatus,
                    request.getFinalScore(),
                    request.getReasonText(),
                    false,
                    false);

            return mapToResponse(firstSide);
        }

        MatchingItem saved = upsertOneSide(
                request.getUserId(),
                request.getRecommendedUserId(),
                actionStatus,
                request.getFinalScore(),
                request.getReasonText(),
                true,
                request.getIsRecommendation()

        );

        return mapToResponse(saved);
    }

    private MatchingItem upsertOneSide(
            Long userId,
            Long recommendedUserId,
            MatchingActionStatus newStatus,
            Double finalScore,
            String reasonText,
            Boolean isUpdate,
            Boolean isRecommendation) {
        MatchingItem matchingItem = matchingItemRepository
                .findByUserIdAndRecommendedUserId(userId, recommendedUserId)
                .orElseGet(() -> {
                    MatchingItem item = new MatchingItem();
                    item.setUserId(userId);
                    item.setRecommendedUserId(recommendedUserId);
                    item.setFinalScore(0.0);
                    item.setIsRecommendation(isRecommendation);
                    return item;
                });

        LocalDateTime now = LocalDateTime.now();

        updateActionTime(matchingItem, newStatus, now);

        if (shouldUpdateStatus(matchingItem.getActionStatus(), newStatus)) {
            matchingItem.setActionStatus(newStatus);
        }

        if (finalScore != null) {
            matchingItem.setFinalScore(finalScore);
        }

        if (reasonText != null) {
            matchingItem.setReasonText(reasonText);
        }

        return matchingItemRepository.save(matchingItem);
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

        if (isFinalStatus(currentStatus)) {
            return false;
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
}