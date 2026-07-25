package com.example.microservice.controller;

import com.example.microservice.dto.request.CreateReportRequest;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.ReportResponse;
import com.example.microservice.dto.respone.ReportOptionResponse;
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

import java.util.List;
import java.util.Map;

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

    @GetMapping("/unresolved-counts")
    public ResponseEntity<ApiResponse<Map<Long, Long>>> getUnresolvedReportCounts(
            @RequestParam ReportTargetType targetType,
            @RequestParam List<Long> targetIds) {
        Map<Long, Long> counts = reportService.getUnresolvedReportCounts(targetType, targetIds);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy số lượng báo cáo chưa xử lý thành công",
                counts));
    }

    @GetMapping("/target-types")
    public ResponseEntity<ApiResponse<List<ReportOptionResponse>>> getTargetTypes() {
        List<ReportOptionResponse> list = List.of(
                new ReportOptionResponse("USER", "Người dùng"),
                new ReportOptionResponse("POST", "Bài viết"),
                new ReportOptionResponse("GROUP", "Nhóm học"),
                new ReportOptionResponse("DOCUMENT", "Tài liệu học tập")
        );
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy danh sách loại đối tượng báo cáo thành công",
                list));
    }

    @GetMapping("/reasons")
    public ResponseEntity<ApiResponse<List<ReportOptionResponse>>> getReasons() {
        List<ReportOptionResponse> list = List.of(
                new ReportOptionResponse("SPAM", "Spam / Quảng cáo"),
                new ReportOptionResponse("HARASSMENT", "Quấy rối / Đe dọa"),
                new ReportOptionResponse("INAPPROPRIATE_CONTENT", "Nội dung không phù hợp"),
                new ReportOptionResponse("FAKE_INFORMATION", "Thông tin giả mạo"),
                new ReportOptionResponse("SCAM", "Lừa đảo"),
                new ReportOptionResponse("CHEATING", "Gian lận"),
                new ReportOptionResponse("COPYRIGHT", "Vi phạm bản quyền"),
                new ReportOptionResponse("INCORRECT_SUBJECT", "Sai môn học"),
                new ReportOptionResponse("MALWARE_OR_UNSAFE", "Mã độc hoặc không an toàn"),
                new ReportOptionResponse("DUPLICATE", "Trùng lặp"),
                new ReportOptionResponse("OTHER", "Khác")
        );
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy danh sách lý do báo cáo thành công",
                list));
    }
}
