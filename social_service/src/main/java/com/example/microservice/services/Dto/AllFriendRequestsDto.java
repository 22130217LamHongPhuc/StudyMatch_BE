package com.example.microservice.services.Dto;

import lombok.Data;
import java.util.List;

@Data
public class AllFriendRequestsDto {
    private List<FriendRequestDto> sent;
    private List<FriendRequestDto> received;
}

