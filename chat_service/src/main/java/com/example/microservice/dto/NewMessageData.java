package com.example.microservice.dto;

import com.example.microservice.entity.Message;

public class NewMessageData {
    private Long conversationId;
    private Message message;

    public NewMessageData() {
    }

    public NewMessageData(Long conversationId, Message message) {
        this.conversationId = conversationId;
        this.message = message;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}