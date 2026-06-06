package com.example.microservice.socket;

public class PresenceChangedEvent {
    private final Long userId;
    private final boolean online;

    public PresenceChangedEvent(Long userId, boolean online) {
        this.userId = userId;
        this.online = online;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isOnline() {
        return online;
    }
}
