package com.example.microservice.clients;

import com.example.microservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/{userId}/fullname")
    ApiResponse<String> getFullName(@PathVariable("userId") Long userId);
}
