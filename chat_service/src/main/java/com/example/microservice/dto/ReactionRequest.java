package com.example.microservice.dto;
import lombok.Data;


@Data
public class ReactionRequest {
    Long conversationID;
    Long  messageID;
    String  emoji;
}
