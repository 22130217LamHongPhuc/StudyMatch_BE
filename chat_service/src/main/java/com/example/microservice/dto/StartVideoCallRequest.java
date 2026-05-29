package com.example.microservice.dto;

import lombok.Data;

@Data
public class StartVideoCallRequest {
    private Long conversationId;
    private String callerName;
    private String callerAvatar;
    private String callType;
}
