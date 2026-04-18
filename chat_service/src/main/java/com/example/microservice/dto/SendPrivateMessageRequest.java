package com.example.microservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SendPrivateMessageRequest {
    private Long senderId;
    private Long to;
    private String content;
    private String messageType;
    private Long replyToMessageId;
}