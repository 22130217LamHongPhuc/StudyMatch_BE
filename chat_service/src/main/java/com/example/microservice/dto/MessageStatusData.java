package com.example.microservice.dto;

import java.time.Instant;
import java.util.List;

public class MessageStatusData {
    private Long conversationId;
    private Long userId;
    private String status;
    private List<Long> messageIds;
    private Instant at;

    public MessageStatusData() {
    }

    public MessageStatusData(Long conversationId, Long userId, String status, List<Long> messageIds, Instant at) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.status = status;
        this.messageIds = messageIds;
        this.at = at;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<Long> messageIds) {
        this.messageIds = messageIds;
    }

    public Instant getAt() {
        return at;
    }

    public void setAt(Instant at) {
        this.at = at;
    }
}
