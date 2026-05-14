package com.example.microservice.service;

import com.example.microservice.dto.respone.MutualFriendsDto;
import com.example.microservice.dto.respone.ProfileDto;
import com.example.microservice.entity.User;
import com.example.microservice.feignAPI.SocialClient;
import com.example.microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
