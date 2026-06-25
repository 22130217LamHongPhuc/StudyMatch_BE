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
    private Boolean pinned;
    private Boolean isPinned;
    private String moderationStatus;
    private Long replyToMessageId;
    private Long replyToSenderId;
    private String replyToType;
    private String replyToContent;
    private String replyToMediaURL;
    private String replyToFileName;
    private Boolean replyToDeleted;

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
        this.setModerationStatus(message.getModerationStatus());
        boolean messagePinned = "Y".equalsIgnoreCase(message.getPinned());
        this.setPinned(messagePinned);
        this.setIsPinned(messagePinned);
        if (message.getReplyTo() != null) {
            Message replyTo = message.getReplyTo();
            boolean replyDeleted = Boolean.TRUE.equals(replyTo.getIsDeleted());
            this.setReplyToMessageId(replyTo.getId());
            this.setReplyToSenderId(replyTo.getSenderId());
            this.setReplyToType(replyTo.getType());
            this.setReplyToContent(replyDeleted ? null : replyTo.getContent());
            this.setReplyToMediaURL(replyDeleted ? null : replyTo.getMediaUrl());
            this.setReplyToFileName(replyDeleted ? null : replyTo.getFileName());
            this.setReplyToDeleted(replyDeleted);
        }
    }
    public MessDTO(){

    }
}
