package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.CreatePrivateConversationRequest;
import com.example.microservice.dto.MessageRequestDTO;
import com.example.microservice.dto.MessageStatusData;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.entity.PrivateConversation;
import com.example.microservice.handle.ResponseStatus;
import com.example.microservice.services.ChatService;
import com.example.microservice.services.MessageService;
import com.example.microservice.services.MessageStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.microservice.entity.Conversation;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import com.example.microservice.entity.MessageStatus;

@RestController
@RequestMapping("/conversation")
@CrossOrigin(origins = "*")
public class Chat2 {
    @Autowired
    ChatService serivce;
    @Autowired
    MessageService messService;
    @Autowired
    MessageStatusService messageStatusService;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    com.example.microservice.repository.MessageRepo messageRepo;

    @GetMapping
    public ResponseEntity<?> getMess(@RequestParam Long currentUser, @RequestParam Long targetUser, @RequestParam Long page){
        boolean exist = serivce.checkExistConver2User(currentUser, targetUser);
        APIResponse<  Map> apiResponse;
        if(!exist){
            if (page != 0) {
                apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, null);
                return ResponseEntity.ok(apiResponse);
            }

            CreatePrivateConversationRequest request = new CreatePrivateConversationRequest();
            request.setUser1Id(currentUser);
            request.setUser2Id(targetUser);
            PrivateConversation privateConversation = serivce.createPrivateConversation(request);

            Map<String, Object> map = new HashMap<>();
            map.put("conversationId", privateConversation.getId());
            map.put("listMess", List.of());
            apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, map);
            return ResponseEntity.ok(apiResponse);
        }

        Map<String, Object> map = new HashMap<>();
        Long conversationId = serivce.findConvIdByUser(currentUser, targetUser);
        map.put("conversationId", conversationId);
        Conversation conv = serivce.checkConversation(conversationId).orElse(null);
        map.put("color", conv != null ? conv.getColor() : null);
        map.put("font", conv != null ? conv.getFont() : null);
        List<MessDTO> list = messService.getListMessWithStatus(conversationId, currentUser, targetUser, page);
        map.put("listMess", list);
        map.put("seenStatus", getConversationSeenStatus(conversationId));
        if (page == 0) {
            markLoadedIncomingMessagesSeen(conversationId, currentUser, targetUser, list);
        }
        apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, map);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/group")
    public ResponseEntity<?> getGroupMess(
            @RequestParam Long currentUser,
            @RequestParam Long groupId,
            @RequestParam Long page
    ) {
        APIResponse<Map> apiResponse;
        Long conversationId = serivce.ensureGroupConversation(groupId, currentUser).orElse(null);
        if (conversationId == null || !serivce.isParticipant(conversationId, currentUser)) {
            apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, null);
            return ResponseEntity.ok(apiResponse);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("conversationId", conversationId);
        Conversation conv = serivce.checkConversation(conversationId).orElse(null);
        map.put("color", conv != null ? conv.getColor() : null);
        map.put("font", conv != null ? conv.getFont() : null);
        List<MessDTO> list = messService.getListMess(conversationId, page);
        map.put("listMess", list);
        map.put("seenStatus", getConversationSeenStatus(conversationId));

        if (page == 0) {
            List<Long> incomingMessageIds = list.stream()
                    .filter(message -> !currentUser.equals(message.getSenderId()))
                    .map(MessDTO::getMessageId)
                    .filter(messageId -> messageId != null && messageId > 0)
                    .toList();
            if (!incomingMessageIds.isEmpty()) {
                messageStatusService.markSeen(conversationId, currentUser, incomingMessageIds);
            }
        }

        apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, map);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/group/{groupId}/sync-participants")
    public ResponseEntity<Void> syncGroupParticipants(@PathVariable Long groupId, @RequestBody(required = false) Map<String, Object> body) {
        serivce.syncGroupConversationParticipants(groupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/group/pins")
    public ResponseEntity<?> getGroupPins(@RequestParam Long currentUser, @RequestParam List<Long> groupIds) {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (Long groupId : groupIds) {
            Optional<Long> convId = serivce.findGroupConversationId(groupId);
            if (convId.isPresent()) {
                Map<String, Object> map = new HashMap<>();
                map.put("groupId", groupId);
                map.put("conversationId", convId.get());
                map.put("pinned", false);

                com.example.microservice.entity.Message latestMessage = serivce.getLatestMessage(convId.get()).orElse(null);
                if (latestMessage != null) {
                    map.put("lastMessage", new MessDTO(latestMessage));
                }

                Long conversationId = convId.get();
                Long lastSeenMessageId = messageStatusService.findStatus(conversationId, currentUser)
                        .map(status -> status.getLastSeenMessage() != null ? status.getLastSeenMessage().getId() : null)
                        .orElse(null);

                long unreadCount = 0;
                if (latestMessage != null && !currentUser.equals(latestMessage.getSenderId())) {
                    unreadCount = messageRepo.countUnreadMessages(conversationId, currentUser, lastSeenMessageId);
                }
                map.put("unreadCount", unreadCount);

                list.add(map);
            }
        }
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, list));
    }


    @GetMapping("/by-id")
    public ResponseEntity<?> getByConversationId(
            @RequestParam Long currentUser,
            @RequestParam Long conversationId,
            @RequestParam Long page
    ) {
        APIResponse<Map> apiResponse;
        if (!serivce.isParticipant(conversationId, currentUser)) {
            apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, null);
            return ResponseEntity.ok(apiResponse);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("conversationId", conversationId);
        Conversation conv = serivce.checkConversation(conversationId).orElse(null);
        map.put("color", conv != null ? conv.getColor() : null);
        map.put("font", conv != null ? conv.getFont() : null);
        List<MessDTO> list = messService.getListMess(conversationId, page);
        map.put("listMess", list);

        if (page == 0) {
            List<Long> incomingMessageIds = list.stream()
                    .filter(message -> !currentUser.equals(message.getSenderId()))
                    .map(MessDTO::getMessageId)
                    .filter(messageId -> messageId != null && messageId > 0)
                    .toList();
            if (!incomingMessageIds.isEmpty()) {
                messageStatusService.markSeen(conversationId, currentUser, incomingMessageIds);
            }
        }

        apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, map);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/message-requests")
    public ResponseEntity<?> getMessageRequests(@RequestParam Long currentUser) {
        List<MessageRequestDTO> requests = serivce.getPendingMessageRequests(currentUser);
        APIResponse<List<MessageRequestDTO>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, requests);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/accepted-direct")
    public ResponseEntity<?> getAcceptedDirectConversations(@RequestParam Long currentUser) {
        List<MessageRequestDTO> conversations = serivce.getAcceptedDirectConversations(currentUser);
        APIResponse<List<MessageRequestDTO>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, conversations);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{conversationId}/media-files")
    public ResponseEntity<?> getMediaAndFiles(
            @PathVariable Long conversationId,
            @RequestParam Long currentUser
    ) {
        if (!serivce.isParticipant(conversationId, currentUser)) {
            return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, List.of()));
        }

        List<MessDTO> list = messService.getMediaAndFiles(conversationId);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, list));
    }

    @PutMapping("/{conversationId}/color")
    public ResponseEntity<?> updateConversationColor(
            @PathVariable Long conversationId,
            @RequestParam String color
    ) {
        Conversation conv = serivce.checkConversation(conversationId).orElse(null);
        if (conv == null) {
            return ResponseEntity.badRequest().body(new APIResponse<>(ResponseStatus.NOT_FOUND, "Conversation not found"));
        }
        conv.setColor(color);
        serivce.save(conv);

        // Broadcast color change to all participants
        List<Long> participants = serivce.findConversationParticipants(conversationId);
        Map<String, Object> data = new HashMap<>();
        data.put("conversationId", conversationId);
        data.put("color", color);
        SocketEnvelope<Map<String, Object>> response = new SocketEnvelope<>("CONVERSATION_COLOR_CHANGED", data);
        
        for (Long participantId : participants) {
            messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", response);
        }

        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, "Color updated"));
    }

    @PutMapping("/{conversationId}/font")
    public ResponseEntity<?> updateConversationFont(
            @PathVariable Long conversationId,
            @RequestParam String font
    ) {
        Conversation conv = serivce.checkConversation(conversationId).orElse(null);
        if (conv == null) {
            return ResponseEntity.badRequest().body(new APIResponse<>(ResponseStatus.NOT_FOUND, "Conversation not found"));
        }
        conv.setFont(font);
        serivce.save(conv);

        // Broadcast font change to all participants
        List<Long> participants = serivce.findConversationParticipants(conversationId);
        Map<String, Object> data = new HashMap<>();
        data.put("conversationId", conversationId);
        data.put("font", font);
        SocketEnvelope<Map<String, Object>> response = new SocketEnvelope<>("CONVERSATION_FONT_CHANGED", data);
        
        for (Long participantId : participants) {
            messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", response);
        }

        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, "Font updated"));
    }

    private void markLoadedIncomingMessagesSeen(
            Long conversationId,
            Long currentUser,
            Long targetUser,
            List<MessDTO> messages
    ) {
        List<Long> incomingMessageIds = messages.stream()
                .filter(message -> !currentUser.equals(message.getSenderId()))
                .map(MessDTO::getMessageId)
                .filter(messageId -> messageId != null && messageId > 0)
                .toList();
        if (incomingMessageIds.isEmpty()) {
            return;
        }

        messageStatusService.markSeen(conversationId, currentUser, incomingMessageIds);

        MessageStatusData data = new MessageStatusData(
                conversationId,
                currentUser,
                "SEEN",
                incomingMessageIds,
                Instant.now()
        );
        SocketEnvelope<MessageStatusData> response =
                new SocketEnvelope<>(EnumEvent.MESSAGE_SEEN.toString(), data);
        messagingTemplate.convertAndSendToUser(String.valueOf(targetUser), "/queue/chat", response);
    }

    private List<Map<String, Object>> getConversationSeenStatus(Long conversationId) {
        List<MessageStatus> statuses = messageStatusService.getStatusesByConversation(conversationId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (MessageStatus status : statuses) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", status.getUserId());
            map.put("lastSeenMessageId", status.getLastSeenMessage() != null ? status.getLastSeenMessage().getId() : null);
            result.add(map);
        }
        return result;
    }
}
