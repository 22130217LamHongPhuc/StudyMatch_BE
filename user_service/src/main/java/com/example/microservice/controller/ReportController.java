package com.example.microservice.controller;

import com.example.microservice.dto.request.CreateReportRequest;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.ReportResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.ReportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class ReportController {

    ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @RequestHeader("X-User-Id") Long reporterUserId,
            @Valid @RequestBody CreateReportRequest request) {
        ReportResponse response = reportService.createReport(reporterUserId, request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Báo cáo đã được gửi thành công",
                response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> getMyReports(
            @RequestHeader("X-User-Id") Long reporterUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReportResponse> response = reportService.getMyReports(reporterUserId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy danh sách báo cáo thành công",
                response));
    }

    @GetMapping("/my/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponse>> getMyReportDetail(
            @RequestHeader("X-User-Id") Long reporterUserId,
            @PathVariable Long reportId) {
        ReportResponse response = reportService.getMyReportDetail(reporterUserId, reportId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy chi tiết báo cáo thành công",
                response));
    }
}
