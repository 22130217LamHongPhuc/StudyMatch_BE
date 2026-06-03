package com.example.microservice.dto;

import com.example.microservice.entity.Message;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class MessDTO {
    private Long messageId;
    private Long senderId;
    private String type;
    private String content;
    private String mediaURL;
    private String fileName;
    private LocalDateTime createdAt;
    private String status;
    private List<ReactionDTO> reactions;
    private Boolean isDeleted;

    public MessDTO (Message message){
        boolean deleted = Boolean.TRUE.equals(message.getIsDeleted());
        this.setCreatedAt(message.getCreatedAt());
        this.setType(message.getType());
        this.setContent(deleted ? null : message.getContent());
        this.setMediaURL(deleted ? null : message.getMediaUrl());
        this.setFileName(deleted ? null : message.getFileName());
        this.setSenderId(message.getSenderId());
        this.setMessageId(message.getId());
        this.setIsDeleted(deleted);
    }
    public MessDTO(){

    }
}
