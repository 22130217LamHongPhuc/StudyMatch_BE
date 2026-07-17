package com.example.microservice.feignAPI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "CHAT-SERVICE")
public interface ChatClient {

    @PostMapping("/api/chat/notify-force-logout")
    void notifyForceLogout(@RequestBody Map<String, Object> requestBody);
}
