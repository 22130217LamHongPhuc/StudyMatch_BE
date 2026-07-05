package com.example.microservice.dto;

import com.example.microservice.enums.MatchingActionStatus;
import com.example.microservice.enums.MatchingRejectType;
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
public class UpdateMatchingItemStatusRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long recommendedUserId;

    @NotNull
    private MatchingActionStatus actionStatus;

    private Double finalScore;

    private String reasonText;

    private MatchingRejectType rejectType;

    private Boolean isRecommendation;
}
