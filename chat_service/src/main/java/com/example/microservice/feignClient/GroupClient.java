package com.example.microservice.feignClient;

import com.example.microservice.dto.AdminGroupDetailResponse;
import com.example.microservice.dto.GroupApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "group-service", url = "http://localhost:8086")
public interface GroupClient {
    @GetMapping("/api/groups/{groupId}/members/active-user-ids")
    GroupApiResponse<List<Long>> getActiveMemberUserIds(@PathVariable("groupId") Long groupId);

    @GetMapping("/api/admin/groups/{groupId}")
    GroupApiResponse<AdminGroupDetailResponse> getAdminGroupDetail(@PathVariable("groupId") Long groupId);

    @GetMapping("/api/groups/user/{userId}")
    GroupApiResponse<List<AdminGroupDetailResponse>> getUserGroups(@PathVariable("userId") Long userId);

    @PostMapping("/api/groups/{groupId}/members/{userId}/kick")
    GroupApiResponse<Void> kickMember(
            @PathVariable("groupId") Long groupId,
            @PathVariable("userId") Long userId
    );
}
