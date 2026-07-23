package com.example.microservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogSaveRequest {
    private Long adminId;
    private String action;
    private String targetId;
    private String targetType;
    private String details;
    private String ipAddress;
}
