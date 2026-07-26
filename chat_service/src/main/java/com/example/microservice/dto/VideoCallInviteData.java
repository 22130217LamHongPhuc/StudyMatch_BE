package com.example.microservice.dto;

import lombok.Data;

@Data
public class VideoCallInviteData {
    private Long sessionId;
    private Long conversationId;
    private String roomId;
    private Long callerId;
    private String callerName;
    private String callerAvatar;
    private String callType;
    private Boolean isGroupCall;

    public VideoCallInviteData(
            Long sessionId,
            Long conversationId,
            String roomId,
            Long callerId,
            String callerName,
            String callerAvatar,
            String callType
    ) {
        this(sessionId, conversationId, roomId, callerId, callerName, callerAvatar, callType, false);
    }

    public VideoCallInviteData(
            Long sessionId,
            Long conversationId,
            String roomId,
            Long callerId,
            String callerName,
            String callerAvatar,
            String callType,
            Boolean isGroupCall
    ) {
        this.sessionId = sessionId;
        this.conversationId = conversationId;
        this.roomId = roomId;
        this.callerId = callerId;
        this.callerName = callerName;
        this.callerAvatar = callerAvatar;
        this.callType = callType;
        this.isGroupCall = isGroupCall;
    }
}
