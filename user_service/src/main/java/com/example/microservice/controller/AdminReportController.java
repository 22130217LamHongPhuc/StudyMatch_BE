package com.example.microservice.controller;

import com.example.microservice.dto.request.UpdateReportStatusRequest;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.PageResponse;
import com.example.microservice.dto.respone.ReportResponse;
import com.example.microservice.enums.ReportReason;
import com.example.microservice.enums.ReportStatus;
import com.example.microservice.enums.ReportTargetType;
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
@RequestMapping("/api/admin/reports")
@CrossOrigin("*")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminReportController {

    ReportService reportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> getAllReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportTargetType targetType,
            @RequestParam(required = false) ReportReason reason,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<ReportResponse> response = reportService.getAllReports(status, targetType, reason, pageable);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy danh sách báo cáo thành công",
                response));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponse>> getReportDetail(
            @PathVariable Long reportId) {
        ReportResponse response = reportService.getReportDetailForAdmin(reportId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy chi tiết báo cáo thành công",
                response));
    }

    @PatchMapping("/{reportId}/status")
    public ResponseEntity<ApiResponse<ReportResponse>> updateReportStatus(
            @PathVariable Long reportId,
            @RequestHeader("X-User-Id") Long adminId,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        ReportResponse response = reportService.updateReportStatus(reportId, adminId, request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Cập nhật trạng thái báo cáo thành công",
                response));
    }
}
