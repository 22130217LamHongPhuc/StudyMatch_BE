package com.example.microservice.dto.respone;

import lombok.Data;

@Data
public class MutualFriendsDto {
    Long mutualFriends;
    String statusFriend;
    boolean friend;
}
