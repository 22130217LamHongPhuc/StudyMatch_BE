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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.attribute.UserPrincipal;
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
    public void sendMessage(SocketRequest<?> mess,  Principal principal){
        Authentication authentication = (Authentication) principal;
        System.out.println("auth nè"+ authentication);
        System.out.println("pricipal nè" + principal);
        String userId = principal.getName();
        System.out.println("user id "+ userId);
//        UserPrincipal userPrincipal =
//        if (userIdObj == null) {
//            System.out.println("Không tìm thấy userId trong session");
//            return;
//        }

//        Long senderId = Long.valueOf(userIdObj.toString());
//        System.out.println("senderId = " + senderId);
//        System.out.println("đây là user gửi nè: " + senderId);
        if(mess.getEvent().equals("SEND_CHAT")){
            SendMessageRequest data = objectMapper.convertValue(
                    mess.getData(),
                    SendMessageRequest.class
            );
            sendChat(data, userId);
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

    public void sendChat(SendMessageRequest mess, String currentUID){
        boolean exist = chatService.checkPrivateExist(Long.valueOf(mess.getConversationId()), Long.valueOf(mess.getSenderId()));
        if(!exist) return;
        try{
            Message message = chatService.saveMess(mess);
            Long currentUser = (long) mess.getSenderId();
            Optional<Long> otherUser = chatService.findUserOther(Long.valueOf(mess.getConversationId()) , currentUser);
            if (otherUser.isEmpty()) {
                return;
            }
            MessDTO messDTO = new MessDTO();
            messDTO.setSenderId(message.getSenderId());
            messDTO.setType(message.getConversation().getConversationType());
            messDTO.setContent(message.getContent());
            messDTO.setMessageId(message.getId());
            messDTO.setFileName(message.getFileName());
            messDTO.setMediaURL(message.getMediaUrl());
            messDTO.setCreatedAt(message.getCreatedAt());
            System.out.println("user can chuyen di"+String.valueOf(otherUser.get()));
            NewMessageData newMess = new NewMessageData(Long.valueOf(mess.getConversationId()), messDTO);
            SocketEnvelope<NewMessageData> response = new SocketEnvelope<NewMessageData>(EnumEvent.NEW_MESSAGE.toString(), newMess);
            SocketEnvelope<NewMessageData> response_ack = new SocketEnvelope<NewMessageData>(EnumEvent.MESSAGE_ACK.toString(), newMess);
            System.out.println("response nè" + response);
            messagingTemplate.convertAndSendToUser( currentUID, "/queue/chat", response_ack );
            messagingTemplate.convertAndSendToUser(String.valueOf(otherUser.get()), "/queue/chat", response);
            System.out.println("lưu thành công nè");
        } catch(Exception ex){
            System.out.println("co loi roi");
        }
    }
}
