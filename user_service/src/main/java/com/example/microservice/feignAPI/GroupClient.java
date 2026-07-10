package com.example.microservice.feignAPI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "GROUP-SERVICE")
public interface GroupClient {

    @GetMapping("/api/groups/{groupId}/exists")
    boolean existsById(@PathVariable Long groupId);
}
