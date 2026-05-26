package com.example.microservice.services.controller;

import com.example.microservice.services.Dto.AllFriendRequestsDto;
import com.example.microservice.services.Dto.FriendRequestDto;
import com.example.microservice.services.Dto.MutualFriendsDto;
import com.example.microservice.services.Dto.RequestFriendsRequest;
import com.example.microservice.services.config.APIResponse;
import com.example.microservice.services.config.ResponseStatus;
import com.example.microservice.services.entity.FriendRequest;
import com.example.microservice.services.service.FriendRequestService;
import com.example.microservice.services.service.FriendService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/social")
// @CrossOrigin(origins = "*")
public class FriendsController {
    @Autowired
    FriendRequestService service;
    @Autowired
    FriendService friendService;

    @PostMapping("/friend-requests/")
    public ResponseEntity<?> requestFriend(@RequestBody @Valid RequestFriendsRequest req){
        System.out.println("nhận req nè"+ req.toString());
        FriendRequest response=  service.friendRequest(req.getSender_id(), req.getReceiver_id());
        return  ResponseEntity.status(HttpStatusCode.valueOf(201)).body(new APIResponse(ResponseStatus.CREATED,response ));
    }

    @GetMapping("/friends/{id}/count")
    public Long getTotalFriends(@PathVariable Long id){
        return friendService.totalFriend(id);
    }

    @GetMapping("/friend-requests/{userId}")
    public ResponseEntity<?> getAllFriendRequests(@PathVariable Long userId,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false, defaultValue ="100") Integer size){
        AllFriendRequestsDto res = service.getAllRequests(userId, page, size);
        return ResponseEntity.ok(new APIResponse(ResponseStatus.SUCCESS, res));
    }

    @GetMapping("/friends/{id}/mutual")
    public MutualFriendsDto getMutualFriends(@PathVariable Long id, @RequestParam Long targetUserId){
        Long mutualFriends = friendService.countMutualFriends(id, targetUserId);
        boolean isFriends = friendService.isFriends(id, targetUserId);
        String status = service.statusFriend(id, targetUserId);
        MutualFriendsDto dto = new MutualFriendsDto();
        dto.setMutualFriends(mutualFriends);
        dto.setStatusFriend(status);
        dto.setFriend(isFriends);
        System.out.println(dto.toString() + "dto nè");
        return dto;

    }


}
