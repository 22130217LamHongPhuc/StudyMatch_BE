package com.example.microservice.services;

import com.example.microservice.feignClient.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Autowired
    private UserClient userClient;




//    public User sendMessage(Long userId) {
//        User user = userClient.getUser(userId);
//        return user;
//    }


}
