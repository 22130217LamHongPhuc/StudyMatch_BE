package com.group_service.clients;

import com.group_service.dto.SessionReminderRequest;
import com.group_service.dto.StudySessionCreatedRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "CHAT-SERVICE")
public interface ChatClient {

    @PostMapping("/api/chat/online-check")
    List<Long> getOnlineUsers(@RequestBody List<Long> userIds);

    @PostMapping("/api/chat/notify-session-reminder")
    void sendSessionReminder(@RequestBody SessionReminderRequest request);

    @PostMapping("/api/chat/notify-session-created")
    void sendSessionCreatedNotification(@RequestBody StudySessionCreatedRequest request);
}
