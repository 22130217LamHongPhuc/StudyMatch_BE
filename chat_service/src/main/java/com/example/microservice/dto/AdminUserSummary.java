package com.example.microservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserSummary {
    @JsonProperty("user_id")
    @JsonAlias("userId")
    private Long userId;

    @JsonProperty("full_name")
    @JsonAlias("fullName")
    private String fullName;

    @JsonProperty("avatar_url")
    @JsonAlias("avatarUrl")
    private String avatarUrl;

    private String email;
}
