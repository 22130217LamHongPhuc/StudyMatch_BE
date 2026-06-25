package com.example.microservice.services.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendRequestDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

