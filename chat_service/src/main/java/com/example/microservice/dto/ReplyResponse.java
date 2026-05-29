package com.example.microservice.dto;

import lombok.Data;

@Data
public class ReplyResponse {
    Long conversationId;
    MessDTO message;
    Long replyMessID;
    MessDTO replyMess;
}
