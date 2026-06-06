package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReactionDTO {
    private Long reactionId;
    private Long messageId;
    private Long senderId;
    private String emoji;
}
