package com.example.microservice.controller;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.FirstPrivateMess;
import com.example.microservice.dto.MessageStatusData;
import com.example.microservice.dto.MessageStatusRequest;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.dto.NewMessageData;
import com.example.microservice.dto.ReactionDTO;
import com.example.microservice.dto.ReactionData;
import com.example.microservice.dto.ReactionRequest;
import com.example.microservice.dto.RecallRequest;
import com.example.microservice.dto.ReplyResponse;
import com.example.microservice.dto.ReplyTextRequest;
import com.example.microservice.dto.SendMessageRequest;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.dto.SocketRequest;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageReaction;
import com.example.microservice.services.ChatService;
import com.example.microservice.services.MessageService;
import com.example.microservice.services.MessageStatusService;
import com.example.microservice.services.ReactionService;
import com.example.microservice.socket.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    MessageService messageService;
    @Autowired
    MessageStatusService messageStatusService;
    @Autowired
    ReactionService reactionService;
    @Autowired
    WebSocketSessionManager sessionManager;

    @MessageMapping("/send")
    public void sendMessage(SocketRequest<?> mess, Principal principal) {
        Authentication authentication = (Authentication) principal;
        String userId = authentication.getName();

        if (mess.getEvent().equals("SEND_CHAT")) {
            SendMessageRequest data = objectMapper.convertValue(
                    mess.getData(),
                    SendMessageRequest.class
            );
            sendChat(data, userId);
        }

        if (mess.getEvent().equals("FIRST_PRIVATE_MESS")) {
            FirstPrivateMess firstPrivateMess = objectMapper.convertValue(
                    mess.getData(),
                    FirstPrivateMess.class
            );
            firstMess(firstPrivateMess);
        }

        if (mess.getEvent().equals("MESSAGE_RECALL")) {
            RecallRequest request = objectMapper.convertValue(
                    mess.getData(),
                    RecallRequest.class
            );
            recallMess(request, Long.valueOf(userId));
        }

        if (mess.getEvent().equals("REACTION_ADD")) {
            ReactionRequest request = objectMapper.convertValue(
                    mess.getData(),
                    ReactionRequest.class
            );
            addReaction(request, Long.valueOf(userId));
        }

        if (mess.getEvent().equals("MESSAGE_DELIVERED")) {
            MessageStatusRequest request = objectMapper.convertValue(
                    mess.getData(),
                    MessageStatusRequest.class
            );
            deliveredMessage(request, Long.valueOf(userId));
        }

        if (mess.getEvent().equals("MESSAGE_SEEN") || mess.getEvent().equals("READ_RECEIPT")) {
            MessageStatusRequest request = objectMapper.convertValue(
                    mess.getData(),
                    MessageStatusRequest.class
            );
            seenMessage(request, Long.valueOf(userId));
        }

        if (mess.getEvent().equals("SEND_REPLY_MESSAGE")) {
            ReplyTextRequest request = objectMapper.convertValue(
                    mess.getData(),
                    ReplyTextRequest.class
            );

            if (request.getType().equals("text")) {
                replyText(request, Long.valueOf(userId));
            }
        }
    }

    public void sendChat(SendMessageRequest request, String currentUID) {
        Long senderId = Long.valueOf(currentUID);
        Long conversationId = Long.valueOf(request.getConversationId());

        request.setSenderId(senderId.intValue());
        boolean exists = chatService.checkPrivateExist(conversationId, senderId);
        if (!exists) return;

        try {
            Message message = chatService.saveMess(request);
            messageStatusService.markSenderSeen(conversationId, senderId, message);

            Optional<Long> receiverIdOpt = chatService.findUserOther(conversationId, senderId);
            if (receiverIdOpt.isEmpty()) return;

            Long receiverId = receiverIdOpt.get();
            MessDTO messDTO = new MessDTO(message);
            NewMessageData newMessageData = new NewMessageData(conversationId, messDTO);

            boolean receiverOnline = sessionManager.isOnline(receiverId);
            if (receiverOnline) {
                messageStatusService.markDelivered(conversationId, receiverId, message);
                SocketEnvelope<NewMessageData> receiverResponse =
                        new SocketEnvelope<>(EnumEvent.NEW_MESSAGE.toString(), newMessageData);
                messagingTemplate.convertAndSendToUser(String.valueOf(receiverId), "/queue/chat", receiverResponse);
            }

            MessageStatusData statusData = new MessageStatusData(
                    conversationId,
                    receiverId,
                    receiverOnline ? "DELIVERED" : "SENT",
                    List.of(message.getId()),
                    Instant.now()
            );
            SocketEnvelope<MessageStatusData> senderResponse = new SocketEnvelope<>(
                    receiverOnline ? EnumEvent.MESSAGE_DELIVERED.toString() : EnumEvent.MESSAGE_SENT.toString(),
                    statusData
            );
            messagingTemplate.convertAndSendToUser(currentUID, "/queue/chat", senderResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deliveredMessage(MessageStatusRequest request, Long userId) {
        Optional<?> otherUID = chatService.findUserOther(request.getConversationID(), userId);
        if (otherUID.isEmpty()) return;

        List<Long> messageIds = normalizeMessageIds(request);
        if (messageIds.isEmpty()) return;

        Message deliveredMessage = messageService.findMessById(messageIds.get(messageIds.size() - 1));
        messageStatusService.markDelivered(request.getConversationID(), userId, deliveredMessage);

        MessageStatusData data = new MessageStatusData(
                request.getConversationID(),
                userId,
                "DELIVERED",
                messageIds,
                Instant.now()
        );
        SocketEnvelope<MessageStatusData> response =
                new SocketEnvelope<>(EnumEvent.MESSAGE_DELIVERED.toString(), data);
        messagingTemplate.convertAndSendToUser(String.valueOf(otherUID.get()), "/queue/chat", response);
    }

    public void seenMessage(MessageStatusRequest request, Long userId) {
        Optional<?> otherUID = chatService.findUserOther(request.getConversationID(), userId);
        if (otherUID.isEmpty()) return;

        List<Long> messageIds = normalizeMessageIds(request);
        if (messageIds.isEmpty()) return;

        messageStatusService.markSeen(request.getConversationID(), userId, messageIds);

        MessageStatusData data = new MessageStatusData(
                request.getConversationID(),
                userId,
                "SEEN",
                messageIds,
                Instant.now()
        );
        SocketEnvelope<MessageStatusData> response =
                new SocketEnvelope<>(EnumEvent.MESSAGE_SEEN.toString(), data);
        socketSendMess(String.valueOf(userId), String.valueOf(otherUID.get()), response, response);
    }

    private List<Long> normalizeMessageIds(MessageStatusRequest request) {
        List<Long> messageIds = new ArrayList<>();
        if (request.getMessageIDs() != null) {
            messageIds.addAll(request.getMessageIDs());
        }
        if (request.getMessageID() != null && !messageIds.contains(request.getMessageID())) {
            messageIds.add(request.getMessageID());
        }
        return messageIds;
    }

    public void replyText(ReplyTextRequest request, Long userId) {
        Message mess = messageService.findMessById(request.getMessageID());
        MessDTO requestMess = new MessDTO(mess);

        Optional<?> otherUID = chatService.findUserOther(mess.getConversation().getId(), userId);
        if (otherUID.isEmpty()) return;

        MessDTO dto = messageService.replyText(request.getMessageID(), userId, request.getContent());
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setReplyMessID(request.getMessageID());
        replyResponse.setConversationId(mess.getConversation().getId());
        replyResponse.setReplyMess(dto);
        replyResponse.setMessage(requestMess);

        SocketEnvelope<ReplyResponse> res = new SocketEnvelope<>(EnumEvent.NEW_MESSAGE.toString(), replyResponse);
        SocketEnvelope<ReplyResponse> resAck = new SocketEnvelope<>(EnumEvent.MESSAGE_ACK.toString(), replyResponse);
        socketSendMess(String.valueOf(userId), String.valueOf(otherUID.get()), res, resAck);
    }

    public void addReaction(ReactionRequest request, Long userId) {
        Optional<?> otherUID = chatService.findUserOther(request.getConversationID(), userId);
        if (otherUID.isEmpty()) return;

        MessageReaction reaction = reactionService.insertReaction(
                request.getMessageID(),
                request.getEmoji(),
                userId
        );
        ReactionData response = new ReactionData();
        response.setConversationId(request.getConversationID());
        response.setMessage(new ReactionDTO(request.getMessageID(), request.getEmoji()));
        SocketEnvelope<ReactionData> res = new SocketEnvelope<>(EnumEvent.REACTION_ADD.toString(), response);
        socketSendMess(String.valueOf(userId), String.valueOf(otherUID.get()), res, res);
    }

    public void recallMess(RecallRequest request, Long userId) {
        Optional<?> otherUID = chatService.findUserOther(request.getConversationID(), userId);
        if (otherUID.isEmpty()) return;

        boolean check = chatService.checkPrivateExist(request.getConversationID(), userId);
        if (!check) return;

        MessDTO dto = messageService.recallMess(request.getConversationID(), request.getMessageID());
        NewMessageData newMess = new NewMessageData(request.getConversationID(), dto);
        SocketEnvelope<NewMessageData> response = new SocketEnvelope<>(EnumEvent.MESSAGE_RECALL.toString(), newMess);
        socketSendMess(String.valueOf(userId), String.valueOf(otherUID.get()), response, response);
    }

    public void firstMess(FirstPrivateMess mess) {
        Conversation conversation = new Conversation();
        conversation.setConversationType("private");
    }

    public void socketSendMess(String currentUID, String otherUID, SocketEnvelope<?> currResponse, SocketEnvelope<?> otherResponse) {
        messagingTemplate.convertAndSendToUser(currentUID, "/queue/chat", currResponse);
        messagingTemplate.convertAndSendToUser(otherUID, "/queue/chat", otherResponse);
    }
}
