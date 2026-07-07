package com.example.microservice.services.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostReactionResponse {
    private Long userId;
    private String fullName;
    private String avatarUrl;
    private String reactionType;
    private boolean isFriend;
    private int mutualFriends;
}
