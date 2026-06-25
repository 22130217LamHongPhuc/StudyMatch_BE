package com.example.microservice.dto;

import lombok.Data;

@Data
public class ForwardMessageRequest {
    private Long sourceMessageID;
    private Long sourceMessageId;
    private Long targetConversationID;
    private Long targetConversationId;

    public Long resolveSourceMessageId() {
        return sourceMessageID != null ? sourceMessageID : sourceMessageId;
    }

    public Long resolveTargetConversationId() {
        return targetConversationID != null ? targetConversationID : targetConversationId;
    }
}
