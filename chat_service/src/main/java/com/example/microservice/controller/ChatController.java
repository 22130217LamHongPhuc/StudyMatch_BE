package com.example.microservice.controller;

import com.example.microservice.dto.ErrorPayload;
import com.example.microservice.dto.MessagePayload;
import com.example.microservice.dto.SendPrivateMessageRequest;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/chat.echo")
    public void echo(){
        messagingTemplate.convertAndSend( "/queue/messages", "hello");
    }
}
