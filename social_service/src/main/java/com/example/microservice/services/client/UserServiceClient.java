package com.example.microservice.services.client;

import com.example.microservice.services.Dto.BasicUserResponse;
import com.example.microservice.services.Dto.UserServiceClientReportRequest;
import com.example.microservice.services.Dto.AuditLogSaveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "USER-SERVICE", url = "${USER_SERVICE_URL:http://localhost:8085}")
public interface UserServiceClient {

    @PostMapping(value = "/api/users/basic-info", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponseWrapper<List<BasicUserResponse>> getBasicUsers(@RequestBody List<Long> userIds);

    @PostMapping(value = "/api/reports", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponseWrapper<Object> createReport(
            @RequestHeader("X-User-Id") Long reporterUserId,
            @RequestBody UserServiceClientReportRequest request
            );

    @GetMapping(value = "/api/reports/unresolved-counts")
    ApiResponseWrapper<Map<String, Long>> getUnresolvedReportCounts(
            @RequestParam("targetType") String targetType,
            @RequestParam("targetIds") List<Long> targetIds
    );

    @PostMapping(value = "/api/super-admin/audit-logs/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponseWrapper<Void> saveAuditLogInternal(@RequestBody AuditLogSaveRequest request);
}
