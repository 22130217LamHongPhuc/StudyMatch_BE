package com.example.microservice.feignClient;

import com.example.microservice.dto.GroupApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "group-service", url = "http://localhost:8086")
public interface GroupClient {
    @GetMapping("/api/groups/{groupId}/members/active-user-ids")
    GroupApiResponse<List<Long>> getActiveMemberUserIds(@PathVariable("groupId") Long groupId);
}
