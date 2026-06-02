package com.example.microservice.controller;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.MessageStatusData;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.dto.NewMessageData;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.dto.TokenValidateResponse;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.services.ChatService;
import com.example.microservice.services.CloudinaryService;
import com.example.microservice.services.ConversationService;
import com.example.microservice.services.NotificationService;
import com.example.microservice.services.MessageStatusService;
import com.example.microservice.socket.WebSocketSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    UserClient client;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    ConversationService conversationService;
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    private  SimpMessagingTemplate messagingTemplate;
    @Autowired
    ChatService chatService;
    @Autowired
    MessageStatusService messageStatusService;
    @Autowired
    WebSocketSessionManager sessionManager;
    @Autowired
    NotificationService notificationService;

    @GetMapping("/presence/online")
    public Map<String, Boolean> getOnlineStatuses(@RequestParam(name = "userIds", required = false) String userIds) {
        if (userIds == null || userIds.isBlank()) {
            return Map.of();
        }
        List<Long> ids = Arrays.stream(userIds.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());
        return notificationService.getOnlineStatusMap(ids);
    }

    @GetMapping("/presence/online-users")
    public Set<Long> getOnlineUsers() {
        return sessionManager.getOnlineUserIds();
    }


    @PostMapping("/media")
    public void uploadMedia (@RequestParam("file") MultipartFile file,
                             @RequestParam("conversationID") Long conversationId,
                             @RequestParam("type") String type,
                             @RequestParam("content") String content,
                             @RequestParam("fileName") String fileName,
                             @RequestHeader("Authorization") String authorization)  {
        TokenValidateResponse response= client.validateToken(authorization);
        if(!response.isValid()){
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = response.getUserId();
        if (!chatService.isParticipant(conversationId, userId)) {
            throw new IllegalArgumentException("User is not a participant of this conversation");
        }
        Map result = cloudinaryService.uploadFile(file);
        String fileUrl = result.get("secure_url").toString();
//        String fileName = result.get("display_name").toString();
        String resourceType = result.get("resource_type").toString();
        String format = result.get("format").toString();
        String fileType = resourceType + "/" + format;
        Long fileSize = Long.valueOf(result.get("bytes").toString());
        System.out.println(result + "upload ảnh nè");
        Conversation conversation = conversationService.findById(conversationId);
        if(conversation == null){
            throw new RuntimeException("conversation không tồn tại");
        }

        Message mess = new Message();
        mess.setContent(content.trim().length()==0?null:content);
        mess.setSenderId(userId);
        mess.setConversation(conversation);
        mess.setType(type);
        mess.setCreatedAt(LocalDateTime.now());
        mess.setMediaUrl(fileUrl);
        mess.setIsDeleted(false);
        mess.setIsEdited(false);
        mess.setFileName(fileName);
        mess.setFileSize(fileSize);
        Message res = messageRepo.save(mess);
        messageStatusService.markSenderSeen(conversationId, userId, res);
        MessDTO dto = new MessDTO(res);
        NewMessageData newMess = new NewMessageData(Long.valueOf(conversationId), dto);
        SocketEnvelope<NewMessageData> re = new SocketEnvelope<NewMessageData>(EnumEvent.NEW_MESSAGE.toString(), newMess);
        List<Long> participants = chatService.findConversationParticipants(conversationId);
        if (participants.isEmpty()) {
            return ;
        }

        MessageStatusData statusData = new MessageStatusData(
                conversationId,
                userId,
                "SENT",
                List.of(res.getId()),
                Instant.now()
        );
        SocketEnvelope<MessageStatusData> responseAck = new SocketEnvelope<>(
                EnumEvent.MESSAGE_SENT.toString(),
                statusData
        );

        messagingTemplate.convertAndSendToUser( String.valueOf(userId), "/queue/chat", responseAck );
        SocketEnvelope<NewMessageData> senderMessageAck =
                new SocketEnvelope<>(EnumEvent.MESSAGE_ACK.toString(), newMess);
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/chat", senderMessageAck);
        for (Long participantId : participants) {
            if (participantId == null || participantId.equals(userId)) {
                continue;
            }
            messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", re);
        }
    }


}
