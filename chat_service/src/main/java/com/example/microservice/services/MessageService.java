package com.example.microservice.services;

import com.example.microservice.config.APIResponse;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.entity.Message;
import com.example.microservice.handle.ResponseStatus;
import com.example.microservice.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageRepo messageRepo;
    public Page<Message> getConversation(Long conversationId, Pageable pageable){
        return messageRepo.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }

    public   List<MessDTO> getListMess(Long conversationId, Long page){
        Pageable pageable = (Pageable) PageRequest.of(0, 25);
        Page<Message> messages = getConversation(conversationId, pageable);
        List<MessDTO> list = new ArrayList<>();
        for (Message mess : messages){
            MessDTO dto = new MessDTO();
            dto.setMessageId(mess.getId());
            dto.setContent(mess.getContent());
            dto.setType(mess.getType());
            dto.setFileName(mess.getFileName());
            dto.setMediaURL(mess.getMediaUrl());
            dto.setSenderId(mess.getSenderId());
            list.add(dto);
        }
          return list;
    }



}
