package com.example.microservice.services;

import com.example.microservice.dto.CreatePrivateConversationRequest;
import com.example.microservice.dto.GroupApiResponse;
import com.example.microservice.dto.SendMessageRequest;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.ConversationParticipant;
import com.example.microservice.entity.GroupConversation;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.PrivateConversation;
import com.example.microservice.feignClient.GroupClient;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.repository.ConversationParticipantRepo;
import com.example.microservice.repository.ConversationRepo;
import com.example.microservice.repository.GroupConversationRepo;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.PrivateConversationRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private PrivateConversationRepo privateConversationRepo;
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    ConversationRepo conversationRepo;
    @Autowired
    ConversationParticipantRepo conversationParticipantRepo;
    @Autowired
    GroupConversationRepo groupConversationRepo;
    @Autowired
    MessageStatusService messageStatusService;
    @Autowired
    GroupClient groupClient;
//    public User sendMessage(Long userId) {
//        User user = userClient.getUser(userId);
//        return user;
//    }

    public boolean checkPrivateExist(Long conversationId, Long userId){
        Optional<PrivateConversation> conversation =
                privateConversationRepo.findByConversationIdAndUserId(conversationId, userId);
        System.out.println(conversation);
        return !conversation.isEmpty();
    }

    public boolean checkGroupExist(Long conversationId, Long userId) {
        Optional<Long> groupId = findGroupIdByConversationId(conversationId);
        if (groupId.isPresent()) {
            Optional<List<Long>> activeMemberIds = fetchActiveGroupMemberIdsSafely(groupId.get());
            if (activeMemberIds.isPresent()) {
                return activeMemberIds.get().contains(userId);
            }
        }
        return conversationParticipantRepo.existsActiveParticipant(conversationId, userId);
    }

    public boolean isParticipant(Long conversationId, Long userId) {
        String type = getConversationType(conversationId);
        if (isPrivateConversationType(type)) {
            return checkPrivateExist(conversationId, userId);
        }
        if (isGroupConversationType(type)) {
            return checkGroupExist(conversationId, userId);
        }
        return checkPrivateExist(conversationId, userId) || checkGroupExist(conversationId, userId);
    }

    public Long findConvIdByUser(Long user1, Long user2){
        return privateConversationRepo.findConverIdByUsers(user1,user2 );
    }

    public Optional<Long> findGroupConversationId(Long groupId) {
        return groupConversationRepo.findConversationIdByGroupId(groupId);
    }

    public Optional<Long> findGroupIdByConversationId(Long conversationId) {
        return groupConversationRepo.findGroupIdByConversationId(conversationId);
    }

    @Transactional
    public Optional<Long> ensureGroupConversation(Long groupId, Long currentUserId) {
        if (groupId == null) {
            return Optional.empty();
        }

        List<Long> memberIds = fetchActiveGroupMemberIds(groupId);
        if (currentUserId == null || !memberIds.contains(currentUserId)) {
            return Optional.empty();
        }

        Long conversationId = findGroupConversationId(groupId).orElse(null);
        Conversation conversation;
        if (conversationId == null) {
            conversation = new Conversation();
            conversation.setConversationType("group");
            conversation.setCreatedAt(Instant.now());
            conversation = conversationRepo.save(conversation);

            GroupConversation groupConversation = new GroupConversation();
            groupConversation.setConversations(conversation);
            groupConversation.setGroupId(groupId);
            groupConversationRepo.save(groupConversation);
            conversationId = conversation.getId();
        } else {
            conversation = conversationRepo.findById(conversationId).orElse(null);
            if (conversation == null) {
                return Optional.empty();
            }
        }

        syncGroupParticipants(conversation, memberIds);
        return Optional.of(conversationId);
    }

    private List<Long> fetchActiveGroupMemberIds(Long groupId) {
        return fetchActiveGroupMemberIdsSafely(groupId).orElse(List.of());
    }

    private Optional<List<Long>> fetchActiveGroupMemberIdsSafely(Long groupId) {
        try {
            GroupApiResponse<List<Long>> response = groupClient.getActiveMemberUserIds(groupId);
            List<Long> memberIds = response != null && response.getData() != null
                    ? response.getData().stream().filter(memberId -> memberId != null).distinct().toList()
                    : List.of();
            return Optional.of(memberIds);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    private void syncGroupParticipants(Conversation conversation, List<Long> memberIds) {
        Instant now = Instant.now();
        List<ConversationParticipant> activeParticipants =
                conversationParticipantRepo.findActiveParticipantsByConversationId(conversation.getId());
        for (ConversationParticipant participant : activeParticipants) {
            if (!memberIds.contains(participant.getUserId())) {
                participant.setLeftAt(now);
                conversationParticipantRepo.save(participant);
            }
        }

        for (Long memberId : memberIds) {
            if (memberId == null) {
                continue;
            }
            ConversationParticipant participant = conversationParticipantRepo
                    .findByConversationIdAndUserId(conversation.getId(), memberId)
                    .orElseGet(() -> {
                        ConversationParticipant created = new ConversationParticipant();
                        created.setConversation(conversation);
                        created.setUserId(memberId);
                        created.setJoinedAt(now);
                        return created;
                    });
            participant.setLeftAt(null);
            participant.setIsMuted(Boolean.FALSE);
            participant.setIsPinned(Boolean.FALSE);
            conversationParticipantRepo.save(participant);
            messageStatusService.ensureStatus(conversation.getId(), memberId);
        }
    }

    public Message saveMess (SendMessageRequest mess){
        Conversation conver = checkConversation(Long.valueOf(mess.getConversationId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy conversation"));
        Message message = new Message();
        message.setContent(mess.getContent());
        message.setConversation(conver);
        message.setSenderId(Long.valueOf(mess.getSenderId()) );
        message.setType(mess.getType());
        message.setCreatedAt(LocalDateTime.now());
        System.out.println(message);
      return  messageRepo.save(message);
    }
    public Optional<Conversation> checkConversation(Long id){
       return conversationRepo.findById(id);
    }

    public Optional<Long> findUserOther(Long conversationId, Long userCurrent){
        return  privateConversationRepo.findOtherUserId(conversationId, userCurrent);
    }

    public List<Long> findConversationParticipants(Long conversationId) {
        String type = getConversationType(conversationId);
        if (isPrivateConversationType(type)) {
            return privateConversationRepo.findParticipantIdsByConversationId(conversationId);
        }
        if (isGroupConversationType(type)) {
            Optional<Long> groupId = findGroupIdByConversationId(conversationId);
            if (groupId.isPresent()) {
                Optional<List<Long>> activeMemberIds = fetchActiveGroupMemberIdsSafely(groupId.get());
                if (activeMemberIds.isPresent()) {
                    return activeMemberIds.get();
                }
            }
            return conversationParticipantRepo.findActiveUserIdsByConversationId(conversationId);
        }
        return conversationParticipantRepo.findActiveUserIdsByConversationId(conversationId);
    }

    public String getConversationType(Long conversationId) {
        return conversationRepo.findById(conversationId)
                .map(Conversation::getConversationType)
                .orElse(null);
    }

    public boolean isPrivateConversationType(String type) {
        if (type == null) {
            return false;
        }
        String normalized = type.trim().toLowerCase();
        return normalized.equals("1") || normalized.equals("private");
    }

    public boolean isGroupConversationType(String type) {
        if (type == null) {
            return false;
        }
        String normalized = type.trim().toLowerCase();
        return normalized.equals("0") || normalized.equals("group");
    }
    public boolean checkExistConver2User (Long user1, Long user2){
        Optional<PrivateConversation> op = privateConversationRepo.findPrivateBetweenTwoUsers(user1, user2);
        return !op.isEmpty();
    }

    @Transactional
    public PrivateConversation createPrivateConversation(CreatePrivateConversationRequest req) {
        if (req.getUser1Id() == null || req.getUser2Id() == null) {
            throw new RuntimeException("Thiếu userId");
        }

        if (req.getUser1Id().equals(req.getUser2Id())) {
            throw new RuntimeException("Không thể tạo cuộc trò chuyện với chính mình");
        }
        Optional<PrivateConversation> existed =
                privateConversationRepo.findPrivateBetweenTwoUsers(
                        req.getUser1Id(),
                        req.getUser2Id()
                );
        if (existed.isPresent()) {
            messageStatusService.createInitialStatuses(
                    existed.get().getId(),
                    req.getUser1Id(),
                    req.getUser2Id()
            );
            return existed.get();
        }
        Conversation conversation = new Conversation();
        conversation.setConversationType("private");
        conversation.setCreatedAt(Instant.now());
        Conversation savedConversation = conversationRepo.save(conversation);
        PrivateConversation privateConversation = new PrivateConversation();
        privateConversation.setConversations(savedConversation);
        privateConversation.setUser1Id(req.getUser1Id());
        privateConversation.setUser2Id(req.getUser2Id());
        PrivateConversation savedPrivateConversation = privateConversationRepo.save(privateConversation);
        messageStatusService.createInitialStatuses(
                savedConversation.getId(),
                req.getUser1Id(),
                req.getUser2Id()
        );
        return savedPrivateConversation;
    }




}
