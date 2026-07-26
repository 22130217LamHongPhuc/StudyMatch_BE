package com.example.microservice.socket;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UserRuntimeState {
    private final Set<String> socketSessionIds = ConcurrentHashMap.newKeySet();

    @Setter
    private volatile ActiveCall activeCall;

    public boolean isOnline() {
        return !socketSessionIds.isEmpty();
    }
}
