package com.example.microservice.feignAPI;

import com.example.microservice.dto.respone.AdminOverviewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "CHAT-SERVICE", url = "${CHAT_SERVICE_URL:}")
public interface ChatClient {

    @PostMapping("/api/chat/notify-force-logout")
    void notifyForceLogout(@RequestBody Map<String, Object> requestBody);

    @GetMapping("/api/admin/overview/online-count")
    long getOnlineUsersCount();

    @GetMapping("/api/admin/overview/messages")
    List<AdminOverviewResponse.MessagesTimelineDto> getMessagesTimeline(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate);
}
