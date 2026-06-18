package com.example.microservice.services;

import com.example.microservice.dto.MessDTO;
import com.example.microservice.dto.ReactionDTO;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageReaction;
import com.example.microservice.entity.MessageStatus;
import com.example.microservice.exception.ResourceNotFoundException;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.ReactionRepo;
import com.example.microservice.repository.ConversationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    MessageStatusService messageStatusService;
    @Autowired
    ReactionRepo reactionRepo;
    @Autowired
    ConversationRepo conversationRepo;

    public Page<Message> getConversation(Long conversationId, Pageable pageable){
        return messageRepo.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }


    public Message getMessById(Long id){
        return messageRepo.findMessageById((long) id);
    }

    public   List<MessDTO> getListMess(Long conversationId, Long page){
        Pageable pageable = (Pageable) PageRequest.of( page.intValue() , 25);
        Page<Message> messages = getConversation(conversationId, pageable);
        List<MessDTO> list = new ArrayList<>();
        for (Message mess : messages){
            MessDTO dto = new MessDTO(mess);
            list.add(dto);
        }
        attachReactions(list);
        return list;
    }

    public List<MessDTO> getListMessWithStatus(Long conversationId, Long currentUserId, Long targetUserId, Long page) {
        Pageable pageable = PageRequest.of(page.intValue(), 25);
        Page<Message> messages = getConversation(conversationId, pageable);
        MessageStatus targetStatus = messageStatusService.findStatus(conversationId, targetUserId)
                .orElse(null);

        List<MessDTO> list = new ArrayList<>();
        for (Message mess : messages) {
            MessDTO dto = new MessDTO(mess);
            if (currentUserId.equals(mess.getSenderId())) {
                dto.setStatus(resolveOutgoingStatus(mess, targetStatus));
            }
            list.add(dto);
        }
        attachReactions(list);
        return list;
    }

    private void attachReactions(List<MessDTO> messages) {
        List<Long> messageIds = messages.stream()
                .map(MessDTO::getMessageId)
                .filter(id -> id != null && id > 0)
                .toList();
        if (messageIds.isEmpty()) {
            return;
        }

        Map<Long, List<ReactionDTO>> reactionsByMessageId = reactionRepo.findByMessageIdIn(messageIds)
                .stream()
                .collect(Collectors.groupingBy(
                        reaction -> reaction.getMessage().getId(),
                        Collectors.mapping(
                                reaction -> new ReactionDTO(
                                        reaction.getId(),
                                        reaction.getMessage().getId(),
                                        reaction.getUserId(),
                                        reaction.getEmoji()
                                ),
                                Collectors.toList()
                        )
                ));

        for (MessDTO message : messages) {
            message.setReactions(reactionsByMessageId.getOrDefault(message.getMessageId(), List.of()));
        }
    }

    private String resolveOutgoingStatus(Message message, MessageStatus targetStatus) {
        if (targetStatus == null) {
            return "SENT";
        }

        Long messageId = message.getId();
        Message lastSeen = targetStatus.getLastSeenMessage();
        if (lastSeen != null && lastSeen.getId() != null && lastSeen.getId() >= messageId) {
            return "SEEN";
        }

        Message lastDelivered = targetStatus.getLastDeliveredMessage();
        if (lastDelivered != null && lastDelivered.getId() != null && lastDelivered.getId() >= messageId) {
            return "DELIVERED";
        }

        return "SENT";
    }

    public MessDTO recallMess (Long conversationId, Long messageId){
        Message   mess = messageRepo.findMessageById(messageId);
        if(mess == null){
            throw new ResourceNotFoundException("message không tồn tại");
        }
        mess.setDeletedAt(LocalDateTime.now());
        mess.setIsDeleted(true);
        Message result =  messageRepo.save(mess);
        result.setContent(null);
        System.out.println(result.toString() + "result reacall nè");
        return  new MessDTO(result);
    }

    public MessDTO recallMess(Long conversationId, Long messageId, Long userId) {
        Message mess = messageRepo.findMessageById(messageId);
        if (mess == null) {
            throw new ResourceNotFoundException("message khong ton tai");
        }
        if (mess.getConversation() == null || !conversationId.equals(mess.getConversation().getId())) {
            throw new ResourceNotFoundException("message khong thuoc conversation");
        }
        if (mess.getSenderId() == null || !mess.getSenderId().equals(userId)) {
            throw new IllegalArgumentException("Chi nguoi gui moi co the go tin nhan");
        }

        mess.setDeletedAt(LocalDateTime.now());
        mess.setIsDeleted(true);
        mess.setContent(null);
        mess.setMediaUrl(null);
        mess.setFileName(null);
        Message result = messageRepo.save(mess);
        return new MessDTO(result);
    }

    public MessDTO setMessagePinned(Long conversationId, Long messageId, boolean pinned) {
        Message mess = messageRepo.findMessageById(messageId);
        if (mess == null) {
            throw new ResourceNotFoundException("message khong ton tai");
        }
        if (mess.getConversation() == null || !conversationId.equals(mess.getConversation().getId())) {
            throw new ResourceNotFoundException("message khong thuoc conversation");
        }
        if (Boolean.TRUE.equals(mess.getIsDeleted())) {
            throw new IllegalArgumentException("Khong the ghim tin nhan da thu hoi");
        }

        mess.setPinned(pinned ? "Y" : "N");
        Message result = messageRepo.save(mess);
        return new MessDTO(result);
    }



    public MessDTO replyText (Long messageID, Long senderId, String content){
        Message mess = messageRepo.findMessageById(messageID);

        if(mess == null){
            throw new ResourceNotFoundException("message không tồn tại");
        }
        Message newMess = new Message();

        newMess.setConversation(mess.getConversation());
        newMess.setType("text");
        newMess.setSenderId(senderId);
        newMess.setContent(content);
        newMess.setReplyTo(mess);
        newMess.setModerationStatus("NONE");
        newMess.setCreatedAt(LocalDateTime.now());
        Message res = messageRepo.save(newMess);
        return  new MessDTO(res);
    }

    public Message forwardMessage(Long sourceMessageId, Long targetConversationId, Long senderId) {
        Message source = findMessById(sourceMessageId);
        if (Boolean.TRUE.equals(source.getIsDeleted())) {
            throw new IllegalArgumentException("Khong the chuyen tiep tin nhan da thu hoi");
        }

        Conversation targetConversation = conversationRepo.findById(targetConversationId)
                .orElseThrow(() -> new ResourceNotFoundException("conversation khong ton tai"));

        Message forwarded = new Message();
        forwarded.setConversation(targetConversation);
        forwarded.setSenderId(senderId);
        forwarded.setType(source.getType());
        forwarded.setContent(source.getContent());
        forwarded.setMediaUrl(source.getMediaUrl());
        forwarded.setFileName(source.getFileName());
        forwarded.setFileSize(source.getFileSize());
        forwarded.setIsDeleted(false);
        forwarded.setIsEdited(false);
        forwarded.setModerationStatus("NONE");
        forwarded.setCreatedAt(LocalDateTime.now());
        return messageRepo.save(forwarded);
    }

    public Message findMessById (Long messageId){
        Message mess = messageRepo.findMessageById(messageId);

        if(mess == null){
            throw new ResourceNotFoundException("message không tồn tại");
        }
        return mess;
    }

}
