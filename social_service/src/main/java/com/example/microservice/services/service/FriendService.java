package com.example.microservice.services.service;


import com.example.microservice.services.repository.FriendRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    @Autowired
    FriendRepo friendRepo;

    public Long totalFriend(Long userId){
        return friendRepo.countTotalFriend(userId);
    }

    public Long countMutualFriends(Long id, Long targetId){
        if (id != null && id.equals(targetId)) {
            return 0L;
        }
        return friendRepo.countMutualFriend(id, targetId);
    }

    public boolean isFriends(Long id, Long targetId){
        if (id != null && id.equals(targetId)) {
            return false;
        }
        Long isFriend = friendRepo.isFriends(id, targetId);
        System.out.println(isFriend+"is friend nè");
        return isFriend != null && isFriend > 0;
    }


    public List<Long> getFriendUserIds(Long userId) {
        return friendRepo.findFriendUserIds(userId);
    }
}
