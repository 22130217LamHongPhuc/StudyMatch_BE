package com.example.microservice.dto.respone;

import com.example.microservice.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentSearchResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String bio;

    public static StudentSearchResponse from(User user) {
        return StudentSearchResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .build();
    }
}
