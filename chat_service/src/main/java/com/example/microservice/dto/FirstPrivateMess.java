package com.example.microservice.dto;

import lombok.Data;

@Data
public class FirstPrivateMess {
    private Long senderId;
    private Long to;
    private String type;
    private String content;
}
