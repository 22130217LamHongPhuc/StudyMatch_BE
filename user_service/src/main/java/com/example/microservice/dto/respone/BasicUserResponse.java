package com.example.microservice.dto.respone;

import com.example.microservice.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasicUserResponse {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("email")
    private String email;

    public static BasicUserResponse from(User user) {
        if (user == null) return null;
        return BasicUserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .email(user.getEmail())
                .build();
    }
}

