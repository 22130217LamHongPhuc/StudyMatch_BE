package com.example.microservice.services.service;

import com.example.microservice.services.entity.FriendRequest;
import com.example.microservice.services.repository.FriendRequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class FriendRequestService {
    @Autowired
    FriendRequestRepo repo;


    public FriendRequest friendRequest(Long senderId,Long reveiverId){
        if (senderId != null && senderId.equals(reveiverId)) {
            throw new IllegalArgumentException("Cannot send a friend request to yourself");
        }
        FriendRequest req = new FriendRequest();
        req.setSenderId(senderId);
        req.setStatus("PENDING");
        req.setReceiverId(reveiverId);
        req.setCreatedAt(getTimeZone());
        req.setUpdatedAt(getTimeZone());
       return repo.save(req);
    }

    public LocalDateTime getTimeZone(){
        Instant instant = Instant.now();
      return LocalDateTime.ofInstant(
                instant,
                ZoneId.of("Asia/Ho_Chi_Minh")
        );
    }

    public String statusFriend(Long id, Long targetId){
        return repo.statusFriends(id, targetId);
    }
}
