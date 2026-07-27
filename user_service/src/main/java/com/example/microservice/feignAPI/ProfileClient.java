package com.example.microservice.feignAPI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PROFILE-SERVICE", url = "${PROFILE_SERVICE_URL:http://localhost:8082}")
public interface ProfileClient {

    @PutMapping("/api/profile/internal/update-info")
    void updateStudentProfileInfo(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "avatarUrl", required = false) String avatarUrl
    );
}
