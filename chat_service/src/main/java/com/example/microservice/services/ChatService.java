package com.example.microservice.services;

import com.example.microservice.dto.MessagePayload;
import com.example.microservice.dto.SendPrivateMessageRequest;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.ConversationParticipant;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.User;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.repository.ConversationParticipantRepository;
import com.example.microservice.repository.ConversationRepository;
import com.example.microservice.repository.MessageRepository;
import com.example.microservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Autowired
    private UserClient userClient;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;



    public User sendMessage(Long userId) {
        User user = userClient.getUser(userId);
        return user;
    }


}
