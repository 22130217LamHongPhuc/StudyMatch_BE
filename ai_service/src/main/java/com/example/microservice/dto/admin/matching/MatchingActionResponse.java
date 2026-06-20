package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.MatchingActionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@Builder
public class MatchingActionResponse {
    private Long id;

    private Long userId;
    private String userFullName;
    private String userAvatarUrl;
    private String userEmail;

    private Long recommendedUserId;
    private String recommendedUserFullName;
    private String recommendedUserAvatarUrl;
    private String recommendedUserEmail;

    private MatchingActionStatus actionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}