package com.group_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "PROFILE-SERVICE", url = "${PROFILE_SERVICE_URL:http://localhost:8082}")
public interface ProfileClient {

    @GetMapping("/api/academic-terms/active")
    Long getActiveTerm();
}