package com.example.microservice.services.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendStatsResponse {
    private Long friendCount;
    private Long pendingReceivedRequestCount;
}
