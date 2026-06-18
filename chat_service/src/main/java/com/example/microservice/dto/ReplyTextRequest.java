package com.example.microservice.dto;

import lombok.Data;

@Data
public class ReplyTextRequest {
    Long conversationId;
    String type;
    String content;
    Long messageID;

}
