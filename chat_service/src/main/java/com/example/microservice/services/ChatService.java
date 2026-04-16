package com.example.microservice.services;

import com.example.microservice.dto.SendMessageRequest;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.PrivateConversation;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.repository.ConversationRepo;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.PrivateConversationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private PrivateConversationRepo privateConversationRepo;
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    ConversationRepo conversationRepo;
//    public User sendMessage(Long userId) {
//        User user = userClient.getUser(userId);
//        return user;
//    }

    public boolean checkPrivateExist(Long conversationId, Long userId){
        Optional<PrivateConversation> conversation =
                privateConversationRepo.findByConversationIdAndUserId(conversationId, userId);
        System.out.println(conversation);
        return !conversation.isEmpty();
    }

    public Message saveMess (SendMessageRequest mess){
        Conversation conver = checkConversation(Long.valueOf(mess.getConversationId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy conversation"));
        Message message = new Message();
        message.setContent(mess.getContent());
        message.setConversation(conver);
        message.setSenderId(Long.valueOf(mess.getSenderId()) );
        message.setType(mess.getType());
        message.setCreatedAt(Instant.now());
        System.out.println(message);
      return  messageRepo.save(message);
    }
    public Optional<Conversation> checkConversation(Long id){
       return conversationRepo.findById(id);
    }

    public Optional<Long> findUserOther(Long conversationId, Long userCurrent){
        return  privateConversationRepo.findOtherUserId(conversationId, userCurrent);
    }
}
