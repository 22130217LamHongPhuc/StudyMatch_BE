package com.example.microservice.services.service;

import com.example.microservice.services.Dto.AuditLogSaveRequest;
import com.example.microservice.services.client.UserServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private HttpServletRequest servletRequest;

    public void log(Long adminId, String action, String targetId, String targetType, String details) {
        String ipAddress = servletRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = servletRequest.getRemoteAddr();
        } else {
            int index = ipAddress.indexOf(",");
            if (index != -1) {
                ipAddress = ipAddress.substring(0, index);
            }
        }

        AuditLogSaveRequest logRequest = AuditLogSaveRequest.builder()
                .adminId(adminId)
                .action(action)
                .targetId(targetId)
                .targetType(targetType)
                .details(details)
                .ipAddress(ipAddress)
                .build();

        try {
            userServiceClient.saveAuditLogInternal(logRequest);
        } catch (Exception e) {
            System.err.println("Failed to save audit log: " + e.getMessage());
        }
    }
}
