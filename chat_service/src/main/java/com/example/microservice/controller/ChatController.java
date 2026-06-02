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
import com.example.microservice.services.MessageDeliveryService;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    MessageDeliveryService messageDeliveryService;
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

        if (mess.getEvent().equals("CLIENT_READY")) {
            messageDeliveryService.sendPendingMessagesToUser(Long.valueOf(userId));
        }
    }

    public void sendChat(SendMessageRequest request, String currentUID) {
        Long senderId = Long.valueOf(currentUID);
        Long conversationId = Long.valueOf(request.getConversationId());

        request.setSenderId(senderId.intValue());
        boolean exists = chatService.isParticipant(conversationId, senderId);
        if (!exists) return;

        try {
            Message message = chatService.saveMess(request);
            messageStatusService.markSenderSeen(conversationId, senderId, message);
            MessDTO messDTO = new MessDTO(message);
            NewMessageData newMessageData = new NewMessageData(conversationId, messDTO);

            List<Long> participants = chatService.findConversationParticipants(conversationId);
            if (participants.isEmpty()) return;

            MessageStatusData statusData = new MessageStatusData(
                    conversationId,
                    senderId,
                    "SENT",
                    List.of(message.getId()),
                    Instant.now()
            );
            SocketEnvelope<MessageStatusData> senderResponse = new SocketEnvelope<>(
                    EnumEvent.MESSAGE_SENT.toString(),
                    statusData
            );
            messagingTemplate.convertAndSendToUser(currentUID, "/queue/chat", senderResponse);
            SocketEnvelope<NewMessageData> senderAck =
                    new SocketEnvelope<>(EnumEvent.MESSAGE_ACK.toString(), newMessageData);
            messagingTemplate.convertAndSendToUser(currentUID, "/queue/chat", senderAck);

            SocketEnvelope<NewMessageData> receiverResponse =
                    new SocketEnvelope<>(EnumEvent.NEW_MESSAGE.toString(), newMessageData);
            for (Long participantId : participants) {
                if (participantId == null || participantId.equals(senderId)) {
                    continue;
                }
                messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", receiverResponse);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deliveredMessage(MessageStatusRequest request, Long userId) {
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
        Set<Long> targets = resolveStatusNotifyTargets(request.getConversationID(), userId, messageIds);
        for (Long targetId : targets) {
            messagingTemplate.convertAndSendToUser(String.valueOf(targetId), "/queue/chat", response);
        }
    }

    public void seenMessage(MessageStatusRequest request, Long userId) {
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
        Set<Long> targets = resolveStatusNotifyTargets(request.getConversationID(), userId, messageIds);
        for (Long targetId : targets) {
            messagingTemplate.convertAndSendToUser(String.valueOf(targetId), "/queue/chat", response);
        }
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
        Long conversationId = request.getConversationID();
        System.out.println("[Reaction][BE][receive] userId=" + userId
                + ", conversationId=" + conversationId
                + ", messageId=" + request.getMessageID()
                + ", emoji=" + request.getEmoji());
        if (conversationId == null || !chatService.isParticipant(conversationId, userId)) {
            System.out.println("[Reaction][BE][skip] user is not participant or missing conversationId, userId="
                    + userId + ", conversationId=" + conversationId);
            return;
        }

        MessageReaction reaction = reactionService.insertReaction(
                request.getMessageID(),
                request.getEmoji(),
                userId
        );
        System.out.println("[Reaction][BE][saved] reactionId=" + reaction.getId()
                + ", userId=" + reaction.getUserId()
                + ", messageId=" + reaction.getMessage().getId()
                + ", emoji=" + reaction.getEmoji());
        ReactionData response = new ReactionData();
        response.setConversationId(conversationId);
        response.setMessage(new ReactionDTO(
                reaction.getId(),
                request.getMessageID(),
                userId,
                reaction.getEmoji()
        ));

        SocketEnvelope<ReactionData> ack =
                new SocketEnvelope<>(EnumEvent.REACTION_ACK.toString(), response);
        SocketEnvelope<ReactionData> receiverEvent =
                new SocketEnvelope<>(EnumEvent.REACTION_ADD.toString(), response);

        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/chat", ack);
        System.out.println("[Reaction][BE][send-ack] targetUserId=" + userId
                + ", event=" + EnumEvent.REACTION_ACK);

        List<Long> participants = chatService.findConversationParticipants(conversationId);
        System.out.println("[Reaction][BE][participants] conversationId=" + conversationId
                + ", participants=" + participants);
        for (Long participantId : participants) {
            if (participantId == null || participantId.equals(userId)) {
                continue;
            }
            messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", receiverEvent);
            System.out.println("[Reaction][BE][send-reaction] targetUserId=" + participantId
                    + ", event=" + EnumEvent.REACTION_ADD);
        }
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

    private Set<Long> resolveStatusNotifyTargets(Long conversationId, Long actorUserId, List<Long> messageIds) {
        String conversationType = chatService.getConversationType(conversationId);
        Set<Long> targets = new LinkedHashSet<>();
        if (chatService.isPrivateConversationType(conversationType)) {
            chatService.findUserOther(conversationId, actorUserId).ifPresent(targets::add);
            return targets;
        }

        for (Long messageId : messageIds) {
            Message message = messageService.findMessById(messageId);
            Long senderId = message.getSenderId();
            if (senderId != null && !senderId.equals(actorUserId)) {
                targets.add(senderId);
            }
        }
        return targets;
    }
}
