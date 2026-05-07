package com.example.microservice.dto.respone;

import lombok.Data;




@Data
public class ProfileResponse {
    String fullName;
    String avatarUrl;
    String bio;
    Long mutualFriend;
    Long numberFriend;

}
