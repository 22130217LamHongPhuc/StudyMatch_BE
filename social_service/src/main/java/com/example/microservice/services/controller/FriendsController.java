package com.example.microservice.services.controller;

import com.example.microservice.services.Dto.*;
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

import java.util.List;

@RestController
@RequestMapping("/social")
 @CrossOrigin(origins = "*")
public class FriendsController {
    @Autowired
    FriendRequestService service;
    @Autowired
    FriendService friendService;

    @PostMapping("/friend-requests/")
    public ResponseEntity<?> requestFriend(@RequestBody @Valid RequestFriendsRequest req){
        System.out.println("nhận req nè"+ req.toString());
        FriendRequest response = service.friendRequest(req.getSender_id(), req.getReceiver_id());
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(new APIResponse<>(ResponseStatus.CREATED, response));
    }

    @GetMapping("/friends/{id}/count")
    public Long getTotalFriends(@PathVariable Long id){
        return friendService.totalFriend(id);
    }

    @GetMapping("/friends/{userId}/stats")
    public ResponseEntity<?> getFriendStats(@PathVariable Long userId){
        FriendStatsResponse res = friendService.getFriendStats(userId);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, res));
    }

    @GetMapping("/friend-requests/{userId}")
    public ResponseEntity<?> getAllFriendRequests(@PathVariable Long userId,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false, defaultValue ="100") Integer size){
        AllFriendRequestsDto res = service.getAllRequests(userId, page, size);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, res));
    }

    @PatchMapping("/friend-requests/{requestId}/status")
    public ResponseEntity<?> updateFriendRequestStatus(@PathVariable Long requestId,
                                                       @RequestBody @Valid UpdateFriendRequestStatusRequest req){
        FriendRequestDto res = service.updateStatus(requestId, req.getStatus());
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, res));
    }

    @PatchMapping("/friend-requests/sender/{senderId}/receiver/{receiverId}/status")
    public ResponseEntity<?> updateFriendRequestStatusBySenderAndReceiver(
            @PathVariable Long senderId,
            @PathVariable Long receiverId,
            @RequestBody @Valid UpdateFriendRequestStatusRequest req){
        FriendRequestDto res = service.updateStatusBySenderAndReceiver(senderId, receiverId, req.getStatus());
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, res));
    }

    @GetMapping("/friends/{userId}/list")
    public ResponseEntity<?> getFriendList(@PathVariable Long userId){
        List<FriendDto> res = friendService.getFriendList(userId);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, res));
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
        System.out.println(dto + "dto nè");
        return dto;
    }

    @DeleteMapping("/friends/unfriend")
    public ResponseEntity<?> unfriend(@RequestParam Long userId, @RequestParam Long friendId) {
        friendService.unfriend(userId, friendId);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, "Hủy kết bạn thành công"));
    }
}

