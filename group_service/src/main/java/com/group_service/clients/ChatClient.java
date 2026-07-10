package com.group_service.clients;

import com.group_service.dto.SessionReminderRequest;
import com.group_service.dto.StudySessionCreatedRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "CHAT-SERVICE")
public interface ChatClient {

    @PostMapping("/api/chat/online-check")
    List<Long> getOnlineUsers(@RequestBody List<Long> userIds);

    @PostMapping("/api/chat/notify-session-reminder")
    void sendSessionReminder(@RequestBody SessionReminderRequest request);

    @PostMapping("/api/chat/notify-session-created")
    void sendSessionCreatedNotification(@RequestBody StudySessionCreatedRequest request);

    @PostMapping("/api/chat/notify-group-invitation")
    void sendGroupInvitationNotification(@RequestBody com.group_service.dto.GroupInvitationNotificationRequest request);

    @PostMapping("/api/chat/notify-group-invitation-status")
    void sendGroupInvitationStatusNotification(
            @RequestBody com.group_service.dto.GroupInvitationNotificationRequest request);

    @PostMapping("/api/chat/notify-group-kick")
    void sendGroupKickNotification(@RequestBody com.group_service.dto.GroupKickNotificationRequest request);

    @PostMapping("/conversation/group/{groupId}/sync-participants")
    void syncGroupParticipants(@PathVariable("groupId") Long groupId);
}
