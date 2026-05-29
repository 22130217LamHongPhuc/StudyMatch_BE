package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoCallResponse {
    private Long sessionId;
    private Long conversationId;
    private Long appId;
    private String roomId;
    private Long userId;
    private String userName;
    private String token;
    private Long tokenExpiredAt;
    private Long targetUserId;
    private String callType;
}
