package com.example.microservice.dto;

import lombok.Data;

@Data
public class ModerationMessageResponse {
    private Long id;
    private String content;
    private String label;
}
