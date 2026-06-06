package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {
    private Long conversationId;
    private Long otherUserId;
    private MessDTO lastMessage;
}
