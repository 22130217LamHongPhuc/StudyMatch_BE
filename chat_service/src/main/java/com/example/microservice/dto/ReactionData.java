package com.example.microservice.dto;

import lombok.Data;

@Data
public class ReactionData {
    Long conversationId;
    ReactionDTO message;
}
