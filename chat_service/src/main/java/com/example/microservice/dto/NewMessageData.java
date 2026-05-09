package com.example.microservice.dto;

import com.example.microservice.entity.Message;
import lombok.Data;

@Data
public class NewMessageData {
    private Long conversationId;
    private MessDTO message;

    public NewMessageData() {
    }

    public NewMessageData(Long conversationId, MessDTO message) {
        this.conversationId = conversationId;
        this.message = message;
    }
}