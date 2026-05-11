package com.example.microservice.services;

import com.example.microservice.config.APIResponse;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.entity.Message;
import com.example.microservice.exception.ResourceNotFoundException;
import com.example.microservice.handle.ResponseStatus;
import com.example.microservice.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageRepo messageRepo;
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
            MessDTO dto = new MessDTO();
            dto.setMessageId(mess.getId());
            dto.setContent(mess.getContent());
            dto.setType(mess.getType());
            dto.setCreatedAt(mess.getCreatedAt());
            dto.setFileName(mess.getFileName());
            dto.setMediaURL(mess.getMediaUrl());
            dto.setSenderId(mess.getSenderId());
            list.add(dto);
        }
          return list;
    }

    public MessDTO recallMess (Long conversationId, Long messageId){
        Message mess = messageRepo.findMessageById(messageId);
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



}
