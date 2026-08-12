package com.example.microservice.services.service;

import com.example.microservice.services.repository.FriendRepo;
import com.example.microservice.services.repository.FriendRequestRepo;
import com.example.microservice.services.repository.UnfriendRepo;
import com.example.microservice.services.repository.UserSkipRepo;
import com.example.microservice.services.Dto.FriendDto;
import com.example.microservice.services.Dto.FriendStatsResponse;

import com.example.microservice.services.Dto.BasicUserResponse;
import com.example.microservice.services.client.UserServiceClient;
import com.example.microservice.services.entity.Unfriend;
import com.example.microservice.services.entity.UserSkip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.microservice.services.Dto.FriendRelationDto;
import com.example.microservice.services.entity.FriendRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
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
    UnfriendRepo unfriendRepo;

    @Autowired
    UserSkipRepo userSkipRepo;
    
    @Autowired
    UserServiceClient userServiceClient;

    @org.springframework.transaction.annotation.Transactional
    public void skipUser(Long userId, Long skippedUserId) {
        UserSkip skip = new UserSkip();
        skip.setUserId(userId);
        skip.setSkippedUserId(skippedUserId);
        userSkipRepo.save(skip);
    }

    @org.springframework.transaction.annotation.Transactional
    public void unfriend(Long userId, Long friendId) {
        friendRepo.deleteFriendship(userId, friendId);
        friendRequestRepo.deleteFriendRequests(userId, friendId);

        Unfriend unfriend = new Unfriend();
        unfriend.setUserId(userId);
        unfriend.setFriendId(friendId);
        unfriendRepo.save(unfriend);
    }

    public Long totalFriend(Long userId) {
        return (long) getFriendList(userId).size();
    }

    public FriendStatsResponse getFriendStats(Long userId) {
        return new FriendStatsResponse(
                totalFriend(userId),
                friendRequestRepo.countByReceiverIdAndStatus(userId, "PENDING"));
    }

    public Long countMutualFriends(Long id, Long targetId) {
        return friendRepo.countMutualFriend(id, targetId);
    }

    public boolean isFriends(Long id, Long targetId) {
        Long isFriend = friendRepo.isFriends(id, targetId);
        System.out.println(isFriend + "is friend nè");
        return isFriend != null && isFriend > 0;
    }

    public List<FriendDto> getFriendList(Long userId) {
        List<Long> friendIds = friendRepo.getFriendListByUserId(userId);
        List<BasicUserResponse> basicUsers = userServiceClient.getBasicUsers(friendIds).getData();

        Map<Long, BasicUserResponse> userMap = basicUsers.stream()
                .collect(Collectors.toMap(BasicUserResponse::getUserId, user -> user));

        return friendIds.stream()
                .filter(userMap::containsKey)
                .map(id -> {
                    BasicUserResponse userInfo = userMap.get(id);
                    return FriendDto.builder()
                            .userId(id)
                            .fullName(userInfo.getFullName())
                            .avatarUrl(userInfo.getAvatarUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public Map<Long, FriendRelationDto> getFriendRelationsMap(Long userId) {
        Map<Long, FriendRelationDto> relations = new HashMap<>();
        Map<Long, LocalDateTime> latestTimes = new HashMap<>();
        LocalDateTime fourteenDaysAgo = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).minusDays(14);

        List<FriendRequest> requests = friendRequestRepo.findAllRelationsByUserId(userId);
        for (FriendRequest fr : requests) {
            boolean isRejected = "REJECTED".equalsIgnoreCase(fr.getStatus());
            if (isRejected && fr.getUpdatedAt().isBefore(fourteenDaysAgo)) {
                continue;
            }
            Long otherUserId = fr.getSenderId().equals(userId) ? fr.getReceiverId() : fr.getSenderId();
            LocalDateTime time = fr.getUpdatedAt();
            if (!latestTimes.containsKey(otherUserId) || time.isAfter(latestTimes.get(otherUserId))) {
                latestTimes.put(otherUserId, time);
                relations.put(otherUserId, FriendRelationDto.builder()
                        .id(fr.getId())
                        .senderId(fr.getSenderId())
                        .receiverId(fr.getReceiverId())
                        .status(fr.getStatus())
                        .build());
            }
        }

        List<UserSkip> skips = userSkipRepo.findRecentSkips(userId, fourteenDaysAgo);
        for (UserSkip skip : skips) {
            Long otherUserId = skip.getSkippedUserId();
            LocalDateTime time = skip.getCreatedAt();
            if (!latestTimes.containsKey(otherUserId) || time.isAfter(latestTimes.get(otherUserId))) {
                latestTimes.put(otherUserId, time);
                relations.put(otherUserId, FriendRelationDto.builder()
                        .id(skip.getId())
                        .senderId(skip.getUserId())
                        .receiverId(skip.getSkippedUserId())
                        .status("SKIP")
                        .build());
            }
        }

        List<Unfriend> unfriends = unfriendRepo.findRecentUnfriends(userId, fourteenDaysAgo);
        for (Unfriend unf : unfriends) {
            Long otherUserId = unf.getUserId().equals(userId) ? unf.getFriendId() : unf.getUserId();
            LocalDateTime time = unf.getCreatedAt();
            if (!latestTimes.containsKey(otherUserId) || time.isAfter(latestTimes.get(otherUserId))) {
                latestTimes.put(otherUserId, time);
                relations.put(otherUserId, FriendRelationDto.builder()
                        .id(unf.getId())
                        .senderId(unf.getUserId())
                        .receiverId(unf.getFriendId())
                        .status("UNFRIEND")
                        .build());
            }
        }

        return relations;
    }

}
