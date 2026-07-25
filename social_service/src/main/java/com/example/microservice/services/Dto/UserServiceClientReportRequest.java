package com.example.microservice.services.Dto;

import lombok.Data;

@Data
public class UserServiceClientReportRequest {
    private String targetType;
    private Long targetId;
    private String reason;
    private String description;
}
