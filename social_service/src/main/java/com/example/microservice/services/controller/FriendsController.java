package com.example.microservice.services.controller;

import com.example.microservice.services.Dto.RequestFriendsRequest;
import com.example.microservice.services.service.FriendRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social/friend-requests")
public class FriendsController {
    @Autowired
    FriendRequestService service;

    
    @PostMapping("/")
    public ResponseEntity<?> requestFriend(@RequestBody @Valid RequestFriendsRequest req){
        System.out.println("nhận req nè"+ req.toString());
        service.friendRequest(req.getSender_id(), req.getReceiver_id());
        return ResponseEntity.ok(200);
    }


}
