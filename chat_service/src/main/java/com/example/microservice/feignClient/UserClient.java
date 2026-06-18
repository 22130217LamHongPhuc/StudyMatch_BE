package com.example.microservice.feignClient;


import com.example.microservice.dto.TokenValidateResponse;
import com.example.microservice.dto.AdminApiResponse;
import com.example.microservice.dto.AdminUserSummary;
import com.example.microservice.dto.PageResponse;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8085/")
public interface  UserClient {
//    @GetMapping("/users/{id}")
//    User getUser(@PathVariable("id") Long id);

    @GetMapping("/api/auth/validate-token")
    TokenValidateResponse validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );

    @PostMapping("/api/users/basic-info")
    AdminApiResponse<List<AdminUserSummary>> getBasicUsers(@RequestBody List<Long> userIds);

    @GetMapping("/api/admin/users")
    AdminApiResponse<PageResponse<AdminUserSummary>> searchAdminUsers(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit,
            @RequestParam("keyword") String keyword
    );
}
