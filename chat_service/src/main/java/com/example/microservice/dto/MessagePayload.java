package com.example.microservice.dto;




import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessagePayload {
    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private Long to;
    private String content;
    private String messageType;
    private Long replyToMessageId;
    private Instant createdAt;
}