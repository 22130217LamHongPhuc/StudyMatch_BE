package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoCallInviteData {
    private Long sessionId;
    private Long conversationId;
    private String roomId;
    private Long callerId;
    private String callerName;
    private String callerAvatar;
    private String callType;
}
