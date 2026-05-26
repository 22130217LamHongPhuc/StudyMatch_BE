package com.example.microservice.dto.respone;


import com.example.microservice.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminUserListItemResponse {

    @JsonProperty("user_id")
    private Long userId;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String role;

    private String status;

    @JsonProperty("is_onboarding_completed")
    private boolean onboardingCompleted;

    @JsonProperty("last_login_at")
    private LocalDateTime lastLoginAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    private String bio;

    public static AdminUserListItemResponse from(User user) {
        return AdminUserListItemResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .onboardingCompleted(user.isOnboardingCompleted())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .emailVerified(user.isEmailVerified())
                .bio(user.getBio())
                .build();
    }
}