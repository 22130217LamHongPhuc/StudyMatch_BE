package com.example.microservice.service;

import com.example.microservice.dto.respone.MutualFriendsDto;
import com.example.microservice.dto.respone.FriendUserDto;
import com.example.microservice.dto.respone.ProfileDto;
import com.example.microservice.entity.User;
import com.example.microservice.feignAPI.SocialClient;
import com.example.microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository repo;
    @Autowired
    SocialClient socialClient;

    public User getProfile (Long userId ){
        return repo.findUsersByUserId(userId);
    }
    public Long getFriendCount(Long userId) {
        return socialClient.getTotalFriends(userId);
    }
    public MutualFriendsDto getMutualFriends(Long id, Long targetId){
        MutualFriendsDto res= socialClient.getMutualFriends(id, targetId);
        System.out.println(res + "nhận về êf");
        return res;
    }
    public ProfileDto getProfile(Long id, Long targetId){
        Long friendsCount = getFriendCount(id);
        MutualFriendsDto mutualDto = getMutualFriends(id, targetId);
        User user = getProfile(targetId);
        ProfileDto  res = new ProfileDto();
        res.setAvatarUrl(user.getAvatarUrl());
        res.setStatusFriend(mutualDto.getStatusFriend());
        res.setBio(user.getBio());
        res.setFullName(user.getFullName());
        res.setMutualFriend(mutualDto.getMutualFriends());
        res.setFriend(mutualDto.isFriend());
        res.setNumberFriend(friendsCount);
        System.out.println(res.toString() + "profile nè");
        return res;
    }

    public List<FriendUserDto> getUsersByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> orderByUserId = java.util.stream.IntStream.range(0, userIds.size())
                .boxed()
                .collect(Collectors.toMap(userIds::get, index -> index, (first, second) -> first));

        return repo.findByUserIdIn(userIds).stream()
                .sorted(Comparator.comparingInt(user -> orderByUserId.getOrDefault(user.getUserId(), Integer.MAX_VALUE)))
                .map(user -> new FriendUserDto(
                        user.getUserId(),
                        user.getFullName(),
                        user.getAvatarUrl(),
                        user.getEmail()
                ))
                .toList();
    }
}
