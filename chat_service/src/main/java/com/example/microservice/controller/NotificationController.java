package com.example.microservice.controller;

import com.example.microservice.dto.SessionReminderRequest;
import com.example.microservice.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/online-check")
    public ResponseEntity<List<Long>> getOnlineUsers(@RequestBody List<Long> userIds) {
        List<Long> onlineUsers = notificationService.getOnlineUsers(userIds);
        return ResponseEntity.ok(onlineUsers);
    }

    @PostMapping("/notify-session-reminder")
    public ResponseEntity<Void> sendSessionReminder(@RequestBody SessionReminderRequest request) {
        notificationService.sendSessionReminder(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notify-group-invitation")
    public ResponseEntity<Void> sendGroupInvitationNotification(@RequestBody com.example.microservice.dto.GroupInvitationNotificationRequest request) {
        notificationService.sendGroupInvitationNotification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notify-group-invitation-status")
    public ResponseEntity<Void> sendGroupInvitationStatusNotification(@RequestBody com.example.microservice.dto.GroupInvitationNotificationRequest request) {
        notificationService.sendGroupInvitationStatusNotification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notify-group-kick")
    public ResponseEntity<Void> sendGroupKickNotification(@RequestBody com.example.microservice.dto.GroupKickNotificationRequest request) {
        notificationService.sendGroupKickNotification(request);
        return ResponseEntity.ok().build();
    }
}
