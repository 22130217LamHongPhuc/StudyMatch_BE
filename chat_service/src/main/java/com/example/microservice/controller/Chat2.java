package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.CreatePrivateConversationRequest;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<MessDTO> list = messService.getListMessWithStatus(conversationId, currentUser, targetUser, page);
        map.put("listMess", list);
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




}
