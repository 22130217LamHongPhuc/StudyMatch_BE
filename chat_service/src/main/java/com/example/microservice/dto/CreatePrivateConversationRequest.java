package com.example.microservice.dto;


import lombok.Data;

@Data
public class CreatePrivateConversationRequest {
    private Long user1Id;
    private Long user2Id;
}