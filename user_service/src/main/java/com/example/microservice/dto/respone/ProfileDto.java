package com.example.microservice.dto.respone;
import lombok.Data;
@Data
public class ProfileDto {
    String fullName;
    String avatarUrl;
    String bio;
    Long mutualFriend;
    Long numberFriend;
    String statusFriend;
    boolean isFriend;
}
