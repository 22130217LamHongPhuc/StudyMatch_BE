package com.example.microservice.services;

import com.example.microservice.dto.MessDTO;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageStatus;
import com.example.microservice.exception.ResourceNotFoundException;
import com.example.microservice.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageRepo messageRepo;
    @Autowired
    MessageStatusService messageStatusService;

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
        return list;
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



    public MessDTO replyText (Long messageID, Long senderId, String content){
        Message mess = messageRepo.findMessageById(messageID);

        if(mess == null){
            throw new ResourceNotFoundException("message không tồn tại");
        }
        Message newMess = new Message();

        newMess.setConversation(mess.getConversation());
        newMess.setType("text");
        newMess.setSenderId(mess.getSenderId());
        newMess.setContent(content);
        newMess.setCreatedAt(LocalDateTime.now());
        Message res = messageRepo.save(newMess);
        return  new MessDTO(res);
    }

    public Message findMessById (Long messageId){
        Message mess = messageRepo.findMessageById(messageId);

        if(mess == null){
            throw new ResourceNotFoundException("message không tồn tại");
        }
        return mess;
    }

}
