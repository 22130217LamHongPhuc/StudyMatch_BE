package com.example.microservice.dto;

public record GroupConversationPinDTO(
        Long groupId,
        Long conversationId,
        Boolean pinned
) {
}
