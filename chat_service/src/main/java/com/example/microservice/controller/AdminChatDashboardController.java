package com.example.microservice.controller;

import com.example.microservice.dto.AdminApiResponse;
import com.example.microservice.dto.AdminChatDashboardResponse;
import com.example.microservice.dto.AdminChatUserViolationResponse;
import com.example.microservice.services.AdminChatDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
public class AdminChatDashboardController {
    private final AdminChatDashboardService adminChatDashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminApiResponse<AdminChatDashboardResponse>> getDashboard(
            @RequestParam(defaultValue = "10") int groupLimit,
            @RequestParam(defaultValue = "10") int memberLimit
    ) {
        AdminChatDashboardResponse response = adminChatDashboardService.getDashboard(groupLimit, memberLimit);

        return ResponseEntity.ok(new AdminApiResponse<>(
                true,
                "SUCCESS",
                "Get chat moderation dashboard successfully",
                response
        ));
    }

    @GetMapping("/users/search")
    public ResponseEntity<AdminApiResponse<AdminChatUserViolationResponse>> searchUserViolations(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "5") int limit
    ) {
        AdminChatUserViolationResponse response = adminChatDashboardService.searchUserViolations(keyword, limit);

        return ResponseEntity.ok(new AdminApiResponse<>(
                true,
                "SUCCESS",
                "Search user chat moderation successfully",
                response
        ));
    }

    @PostMapping("/users/{userId}/groups/{groupId}/kick")
    public ResponseEntity<AdminApiResponse<Void>> kickUserFromGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId
    ) {
        adminChatDashboardService.kickUserFromGroup(userId, groupId);

        return ResponseEntity.ok(new AdminApiResponse<>(
                true,
                "SUCCESS",
                "Kick user from group successfully",
                null
        ));
    }
}
