package com.example.microservice.services.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRelationDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String status;
}
