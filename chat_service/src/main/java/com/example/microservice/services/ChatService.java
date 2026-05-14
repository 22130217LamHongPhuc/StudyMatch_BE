package com.example.microservice.services;

import com.example.microservice.dto.CreatePrivateConversationRequest;
import com.example.microservice.dto.SendMessageRequest;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.PrivateConversation;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.repository.ConversationRepo;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.PrivateConversationRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
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

    public Long findConvIdByUser(Long user1, Long user2){
        return privateConversationRepo.findConverIdByUsers(user1,user2 );
    }

    public Message saveMess (SendMessageRequest mess){
        Conversation conver = checkConversation(Long.valueOf(mess.getConversationId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy conversation"));
        Message message = new Message();
        message.setContent(mess.getContent());
        message.setConversation(conver);
        message.setSenderId(Long.valueOf(mess.getSenderId()) );
        message.setType(mess.getType());
        message.setCreatedAt(LocalDateTime.now());
        System.out.println(message);
      return  messageRepo.save(message);
    }
    public Optional<Conversation> checkConversation(Long id){
       return conversationRepo.findById(id);
    }

    public Optional<Long> findUserOther(Long conversationId, Long userCurrent){
        return  privateConversationRepo.findOtherUserId(conversationId, userCurrent);
    }
    public boolean checkExistConver2User (Long user1, Long user2){
        Optional<PrivateConversation> op = privateConversationRepo.findPrivateBetweenTwoUsers(user1, user2);
        return !op.isEmpty();
    }

    @Transactional
    public PrivateConversation createPrivateConversation(CreatePrivateConversationRequest req) {
        if (req.getUser1Id() == null || req.getUser2Id() == null) {
            throw new RuntimeException("Thiếu userId");
        }

        if (req.getUser1Id().equals(req.getUser2Id())) {
            throw new RuntimeException("Không thể tạo cuộc trò chuyện với chính mình");
        }
        Optional<PrivateConversation> existed =
                privateConversationRepo.findPrivateBetweenTwoUsers(
                        req.getUser1Id(),
                        req.getUser2Id()
                );
        if (existed.isPresent()) {
            return existed.get();
        }
        Conversation conversation = new Conversation();
        conversation.setConversationType("private");
        conversation.setCreatedAt(Instant.now());
        Conversation savedConversation = conversationRepo.save(conversation);
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setConversations(savedConversation);
        privateConversation.setUser1Id(req.getUser1Id());
        privateConversation.setUser2Id(req.getUser2Id());
        return privateConversationRepo.save(privateConversation);
    }




}
