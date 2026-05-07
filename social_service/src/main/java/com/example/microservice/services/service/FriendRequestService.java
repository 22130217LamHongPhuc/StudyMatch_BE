package com.example.microservice.services.service;

import com.example.microservice.services.entity.Friend;
import com.example.microservice.services.entity.FriendRequest;
import com.example.microservice.services.repository.FriendRequestRepo;
import com.netflix.discovery.converters.Auto;
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
