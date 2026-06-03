package com.example.microservice.dto.request;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String fullName;
    private String bio;
    private String avatarUrl;
}
