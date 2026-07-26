package com.example.microservice.socket;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ActiveCall {
    public enum State {
        RINGING,
        CONNECTED
    }

    private final Long sessionId;
    private final Long conversationId;
    private final Long callerId;
    private final Long calleeId;
    private final String callType;
    private final Instant createdAt;
    private volatile Instant acceptedAt;
    private volatile State state;

    public ActiveCall(
            Long sessionId,
            Long conversationId,
            Long callerId,
            Long calleeId,
            String callType
    ) {
        this.sessionId = sessionId;
        this.conversationId = conversationId;
        this.callerId = callerId;
        this.calleeId = calleeId;
        this.callType = callType;
        this.createdAt = Instant.now();
        this.state = State.RINGING;
    }

    public void accept(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
        this.state = State.CONNECTED;
    }

    public boolean involves(Long userId) {
        return callerId.equals(userId) || calleeId.equals(userId);
    }
}
