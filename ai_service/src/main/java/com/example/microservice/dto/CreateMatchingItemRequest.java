package com.example.microservice.dto;

import java.time.LocalDateTime;

import com.example.microservice.enums.MatchingActionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMatchingItemRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long recommendedUserId;

    private Double finalScore;

    private String reasonText;

    @NotNull
    private MatchingActionStatus actionStatus;

    private Boolean isRecommendation;
}