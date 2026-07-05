package com.example.microservice.services.service;


import com.example.microservice.services.repository.FriendRepo;
import com.example.microservice.services.repository.FriendRequestRepo;
import com.example.microservice.services.Dto.FriendDto;
import com.example.microservice.services.Dto.FriendStatsResponse;

import com.example.microservice.services.Dto.BasicUserResponse;
import com.example.microservice.services.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FriendService {
    @Autowired
    FriendRepo friendRepo;

    @Autowired
    FriendRequestRepo friendRequestRepo;
    
    @Autowired
    UserServiceClient userServiceClient;

    @org.springframework.transaction.annotation.Transactional
    public void unfriend(Long userId, Long friendId) {
        friendRepo.deleteFriendship(userId, friendId);
        friendRequestRepo.deleteFriendRequests(userId, friendId);
    }

    public Long totalFriend(Long userId){

        return friendRepo.countTotalFriend(userId);
    }

    public FriendStatsResponse getFriendStats(Long userId) {
        return new FriendStatsResponse(
                friendRepo.countTotalFriend(userId),
                friendRequestRepo.countByReceiverIdAndStatus(userId, "PENDING")
        );
    }

    public Long countMutualFriends(Long id, Long targetId){
        return friendRepo.countMutualFriend(id, targetId);
    }

    public boolean isFriends(Long id, Long targetId){
        Long isFriend = friendRepo.isFriends(id, targetId);
        System.out.println(isFriend+"is friend nè");
        return isFriend != null && isFriend > 0;
    }

    public List<FriendDto> getFriendList(Long userId){
        List<Long> friendIds = friendRepo.getFriendListByUserId(userId);
        List<BasicUserResponse> basicUsers = userServiceClient.getBasicUsers(friendIds).getData();
        
        Map<Long, BasicUserResponse> userMap = basicUsers.stream()
                .collect(Collectors.toMap(BasicUserResponse::getUserId, user -> user));
        
        return friendIds.stream()
                .map(id -> {
                    BasicUserResponse userInfo = userMap.get(id);
                    return FriendDto.builder()
                            .userId(id)
                            .fullName(userInfo != null ? userInfo.getFullName() : null)
                            .avatarUrl(userInfo != null ? userInfo.getAvatarUrl() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

}
