package com.example.microservice.controller;

import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.AdminOverviewResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.AdminOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/overview")
@RequiredArgsConstructor
public class AdminOverviewController {

    private final AdminOverviewService adminOverviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminOverviewResponse>> getAdminOverview(
            @RequestParam(value = "timePreset", required = false, defaultValue = "THIS_WEEK") String timePreset,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        AdminOverviewResponse response = adminOverviewService.getAdminOverview(timePreset, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy dữ liệu thống kê admin thành công", response));
    }
}
