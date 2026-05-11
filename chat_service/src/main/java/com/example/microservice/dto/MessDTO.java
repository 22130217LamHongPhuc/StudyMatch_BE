package com.example.microservice.dto;

import com.example.microservice.entity.Message;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class MessDTO {
    private Long messageId;
    private Long senderId;
    private String type;
    private String content;
    private String mediaURL;
    private String fileName;
    private LocalDateTime createdAt;

    public MessDTO (Message message){
        this.setCreatedAt(message.getCreatedAt());
        this.setType(message.getType());
        this.setContent(message.getContent());
        this.setMediaURL(message.getMediaUrl());
        this.setFileName(message.getFileName());
        this.setSenderId(message.getSenderId());
        this.setMessageId(message.getId());
    }
    public MessDTO(){

    }
}
