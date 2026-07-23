package com.group_service.clients;

import com.group_service.dto.ApiResponse;
import com.group_service.dto.BasicUserResponse;
import com.group_service.dto.SessionReminderEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);

    @PostMapping("/api/users/basic-info")
    ApiResponse<List<BasicUserResponse>> getBasicUsers(@RequestBody List<Long> userIds);

    @PostMapping("/api/users/send-session-reminder")
    void sendSessionReminderEmail(@RequestBody SessionReminderEmailRequest request);

    @PostMapping("/api/users/send-group-lock")
    void sendGroupLockEmail(@RequestBody com.group_service.dto.GroupLockEmailRequest request);

    @GetMapping("/api/auth/validate-token")
    com.group_service.dto.TokenValidateResponse validateToken(
            @org.springframework.web.bind.annotation.RequestHeader("Authorization") String authorization
    );

    @PostMapping("/api/super-admin/audit-logs/internal")
    org.springframework.http.ResponseEntity<ApiResponse<Void>> saveAuditLogInternal(
            @RequestBody com.group_service.dto.AuditLogSaveRequest request
    );
}
