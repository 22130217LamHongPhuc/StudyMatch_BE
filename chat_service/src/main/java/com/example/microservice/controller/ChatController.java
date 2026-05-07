package com.example.microservice.controller;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.*;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.services.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @MessageMapping("/send")
    public void sendMessage(SocketRequest<?> mess,    SimpMessageHeaderAccessor headerAccessor){
        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
        if (userIdObj == null) {
            System.out.println("Không tìm thấy userId trong session");
            return;
        }
        Long senderId = Long.valueOf(userIdObj.toString());
        System.out.println("senderId = " + senderId);
        System.out.println("đây là user gửi nè: " + senderId);
        if(mess.getEvent().equals("SEND_CHAT")){
            SendMessageRequest data = objectMapper.convertValue(
                    mess.getData(),
                    SendMessageRequest.class
            );
            sendChat(data);
        }
        if(mess.getEvent().equals("FIRST_PRIVATE_MESS")){
            FirstPrivateMess firstPrivateMess = objectMapper.convertValue(
                    mess.getData(), FirstPrivateMess.class);
            firstMess(firstPrivateMess);
        }
    }

    public void firstMess(FirstPrivateMess mess){
        Conversation conversation = new Conversation();
        conversation.setConversationType("private");
    }

    public void sendChat(SendMessageRequest mess){
        boolean exist = chatService.checkPrivateExist(Long.valueOf(mess.getConversationId()), Long.valueOf(mess.getSenderId()));
        if(!exist) return;
        try{
            Message message = chatService.saveMess(mess);
            Long currentUser = (long) mess.getSenderId();
            Optional<Long> otherUser = chatService.findUserOther(Long.valueOf(mess.getConversationId()) , currentUser);
            if (otherUser.isEmpty()) {
                return;
            }
            System.out.println("user can chuyen di"+String.valueOf(otherUser.get()));
            NewMessageData newMess = new NewMessageData(Long.valueOf(mess.getConversationId()), message);
            SocketEnvelope<NewMessageData> response = new SocketEnvelope<NewMessageData>(EnumEvent.NEW_MESSAGE.toString(), newMess);
            System.out.println("response nè" + response);
            messagingTemplate.convertAndSend(   "/queue/messages/"+otherUser.get(),response);
            System.out.println("lưu thành công nè");
        } catch(Exception ex){
            System.out.println("co loi roi");
        }
    }
}
