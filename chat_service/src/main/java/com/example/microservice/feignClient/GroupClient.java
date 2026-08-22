package com.example.microservice.feignClient;

import com.example.microservice.dto.AdminGroupDetailResponse;
import com.example.microservice.dto.GroupApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "group-service", url = "${group-service.url:http://localhost:8086}")
public interface GroupClient {
    @GetMapping("/api/groups/{groupId}/members/active-user-ids")
    GroupApiResponse<List<Long>> getActiveMemberUserIds(@PathVariable("groupId") Long groupId);

    @GetMapping("/api/admin/groups/{groupId}")
    GroupApiResponse<AdminGroupDetailResponse> getAdminGroupDetail(@PathVariable("groupId") Long groupId);

    @GetMapping("/api/groups/user/{userId}")
    GroupApiResponse<List<AdminGroupDetailResponse>> getUserGroups(@PathVariable("userId") Long userId);

    @PostMapping(value = "/api/groups/{groupId}/members/{userId}/kick", consumes = MediaType.APPLICATION_JSON_VALUE)
    GroupApiResponse<Void> kickMember(
            @PathVariable("groupId") Long groupId,
            @PathVariable("userId") Long userId,
            @RequestBody(required = false) Map<String, String> body
    );
}