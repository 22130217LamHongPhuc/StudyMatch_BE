package com.example.microservice.controller;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.*;
import com.example.microservice.entity.Message;
import com.example.microservice.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/send")
    public void sendMessage(SocketRequest<SendMessageRequest> mess){
        boolean exist = chatService.checkPrivateExist(Long.valueOf(mess.getData().getConversationId()), Long.valueOf(mess.getData().getSenderId()));
        if(!exist) return;
        try{
            Message message = chatService.saveMess(mess.getData());
            Long currentUser = (long) mess.getData().getSenderId();
            Optional<Long> otherUser = chatService.findUserOther(Long.valueOf(mess.getData().getConversationId()) , currentUser);
            if (otherUser.isEmpty()) {
                return;
            }

            System.out.println("user can chuyen di"+String.valueOf(otherUser.get()));

            NewMessageData newMess = new NewMessageData(Long.valueOf(mess.getData().getConversationId()), message);
            SocketEnvelope<NewMessageData> response = new SocketEnvelope<NewMessageData>(EnumEvent.NEW_MESSAGE.toString(), newMess);
            System.out.println("response nè" + response);
            messagingTemplate.convertAndSend(   "/queue/messages/"+otherUser.get(),response);
            System.out.println("lưu thành công nè");
        } catch(Exception ex){
            System.out.println("co loi roi");
        }
    }
}
