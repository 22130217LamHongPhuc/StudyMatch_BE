package com.example.microservice.dto;

import java.util.List;

public class MessageStatusRequest {
    private Long conversationID;
    private Long messageID;
    private List<Long> messageIDs;

    public Long getConversationID() {
        return conversationID;
    }

    public void setConversationID(Long conversationID) {
        this.conversationID = conversationID;
    }

    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public List<Long> getMessageIDs() {
        return messageIDs;
    }

    public void setMessageIDs(List<Long> messageIDs) {
        this.messageIDs = messageIDs;
    }
}
