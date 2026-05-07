package com.group_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "PROFILE-SERVICE")
public interface ProfileClient {

    @GetMapping("/api/academic-terms/active")
    Long getActiveTerm();
}