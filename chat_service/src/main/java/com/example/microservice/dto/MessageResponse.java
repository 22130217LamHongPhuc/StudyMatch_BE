package com.example.microservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {
    private Long messageId;
    private Integer conversationId;
    private Integer senderId;
    private String content;
    private String type;
    private LocalDateTime createdAt;
}