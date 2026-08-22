package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
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
import com.example.microservice.services.MessageService;
import com.example.microservice.services.MessageModerationService;
import com.example.microservice.socket.WebSocketSessionManager;
import com.example.microservice.handle.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
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
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    ChatService chatService;
    @Autowired
    MessageStatusService messageStatusService;
    @Autowired
    WebSocketSessionManager sessionManager;
    @Autowired
    NotificationService notificationService;
    @Autowired
    MessageService messageService;
    @Autowired
    MessageModerationService messageModerationService;

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
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file,
                                         @RequestParam("conversationID") Long conversationId,
                                         @RequestParam(value = "type", required = false) String type,
                                         @RequestParam(value = "content", required = false) String content,
                                         @RequestParam(value = "fileName", required = false) String fileName,
                                         @RequestHeader("Authorization") String authorization) {
        TokenValidateResponse response = client.validateToken(authorization);
        if (response == null || !response.isValid()) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = response.getUserId();
        if (!chatService.isParticipant(conversationId, userId)) {
            throw new IllegalArgumentException("User is not a participant of this conversation");
        }

        Map result = cloudinaryService.uploadFile(file);
        String fileUrl = "";
        if (result != null) {
            if (result.get("secure_url") != null) {
                fileUrl = result.get("secure_url").toString();
            } else if (result.get("url") != null) {
                fileUrl = result.get("url").toString();
            }
        }

        String resolvedFileName = (fileName != null && !fileName.isBlank()) ? fileName : file.getOriginalFilename();
        if (resolvedFileName == null || resolvedFileName.isBlank()) {
            resolvedFileName = "file";
        }

        Long fileSize = file.getSize();
        if (result != null && result.get("bytes") != null) {
            try {
                fileSize = Long.valueOf(result.get("bytes").toString());
            } catch (Exception ignored) {
            }
        }

        String resolvedType = (type != null && !type.isBlank()) ? type : file.getContentType();
        if (resolvedType == null || resolvedType.isBlank()) {
            resolvedType = "application/octet-stream";
        }

        Conversation conversation = conversationService.findById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found");
        }

        Message mess = new Message();
        mess.setContent((content != null && !content.trim().isEmpty()) ? content.trim() : null);
        mess.setSenderId(userId);
        mess.setConversation(conversation);
        mess.setType(resolvedType);
        mess.setCreatedAt(LocalDateTime.now());
        mess.setMediaUrl(fileUrl);
        mess.setIsDeleted(false);
        mess.setIsEdited(false);
        mess.setFileName(resolvedFileName);
        mess.setFileSize(fileSize);
        mess.setModerationStatus("NONE");
        Message res = messageRepo.save(mess);
        messageStatusService.markSenderSeen(conversationId, userId, res);
        MessDTO dto = new MessDTO(res);
        NewMessageData newMess = new NewMessageData(Long.valueOf(conversationId), dto);
        SocketEnvelope<NewMessageData> re = new SocketEnvelope<NewMessageData>(EnumEvent.NEW_MESSAGE.toString(), newMess);
        List<Long> participants = chatService.findConversationParticipants(conversationId);
        if (participants.isEmpty()) {
            messageModerationService.moderateMessageAsync(res.getId());
            return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, dto));
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

        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/chat", responseAck);
        SocketEnvelope<NewMessageData> senderMessageAck =
                new SocketEnvelope<>(EnumEvent.MESSAGE_ACK.toString(), newMess);
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/chat", senderMessageAck);
        for (Long participantId : participants) {
            if (participantId == null || participantId.equals(userId)) {
                continue;
            }
            messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", re);
        }
        messageModerationService.moderateMessageAsync(res.getId());
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, dto));
    }

    @PatchMapping("/{messageId}/pin")
    public ResponseEntity<?> setMessagePinned(
            @PathVariable Long messageId,
            @RequestParam Long conversationId,
            @RequestParam String pinned
    ) {
        boolean nextPinned = isPinnedValue(pinned);
        MessDTO dto = messageService.setMessagePinned(conversationId, messageId, nextPinned);
        NewMessageData data = new NewMessageData(conversationId, dto);
        SocketEnvelope<NewMessageData> envelope = new SocketEnvelope<>(
                nextPinned ? EnumEvent.MESSAGE_PIN.toString() : EnumEvent.MESSAGE_UNPIN.toString(),
                data
        );

        List<Long> participants = chatService.findConversationParticipants(conversationId);
        for (Long participantId : participants) {
            if (participantId == null) {
                continue;
            }
            messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", envelope);
        }

        APIResponse<MessDTO> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, dto);
        return ResponseEntity.ok(apiResponse);
    }

    private boolean isPinnedValue(String pinned) {
        return "Y".equalsIgnoreCase(pinned) || "true".equalsIgnoreCase(pinned);
    }
}