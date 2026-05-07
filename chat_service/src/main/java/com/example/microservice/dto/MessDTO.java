package com.example.microservice.dto;

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
}
