package com.example.microservice.services.service;


import com.example.microservice.services.repository.FriendRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendService {
    @Autowired
    FriendRepo friendRepo;

    public Long totalFriend(Long userId){
        return friendRepo.countTotalFriend(userId);
    }

    public Long countMutualFriends(Long id, Long targetId){
        return friendRepo.countMutualFriend(id, targetId);
    }

    public boolean isFriends(Long id, Long targetId){
        Long isFriend = friendRepo.isFriends(id, targetId);
        System.out.println(isFriend+"is friend nè");
        return isFriend != null && isFriend > 0;
    }


}
