package com.example.microservice.dto;

import lombok.Data;

@Data
public class ReplyTextRequest {
    String type;
    String content;
    Long messageID;

}
