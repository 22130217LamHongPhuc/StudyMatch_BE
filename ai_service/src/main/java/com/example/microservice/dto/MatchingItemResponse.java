package com.example.microservice.dto;

import com.example.microservice.enums.MatchingActionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingItemResponse {

    private Long id;

    private Long userId;

    private Long recommendedUserId;

    private Double finalScore;

    private String reasonText;

    private MatchingActionStatus actionStatus;

    private LocalDateTime viewedAt;

    private LocalDateTime requestSentAt;

    private LocalDateTime respondedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}