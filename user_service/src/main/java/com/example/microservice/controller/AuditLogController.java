package com.example.microservice.controller;

import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.AuditLogResponse;
import com.example.microservice.dto.respone.AuditLogFiltersResponse;
import com.example.microservice.dto.respone.PageResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.AuditLogService;
import com.example.microservice.service.CustomUserDetails;
import com.example.microservice.exception.AppException;
import com.example.microservice.dto.request.AuditLogSaveRequest;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/audit-logs")
@CrossOrigin("*")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class AuditLogController {

    AuditLogService auditLogService;

    @GetMapping("/filters")
    public ResponseEntity<ApiResponse<AuditLogFiltersResponse>> getFilters() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String callerRole = userDetails.getUser().getRole();
        if (!"super_admin".equalsIgnoreCase(callerRole)) {
            throw new AppException("Không có quyền thực hiện hành động này", StatusCode.ACCESS_DENIED);
        }

        AuditLogFiltersResponse response = auditLogService.getFilters();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy danh sách bộ lọc thành công",
                response
        ));
    }

    @PostMapping("/internal")
    public ResponseEntity<ApiResponse<Void>> saveAuditLogInternal(@RequestBody AuditLogSaveRequest request) {
        auditLogService.saveAuditLog(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lưu nhật ký hoạt động thành công",
                null
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String callerRole = userDetails.getUser().getRole();
        if (!"super_admin".equalsIgnoreCase(callerRole)) {
            throw new AppException("Không có quyền thực hiện hành động này", StatusCode.ACCESS_DENIED);
        }

        PageResponse<AuditLogResponse> response = auditLogService.getAuditLogs(page, limit, keyword, action, targetType);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Lấy danh sách nhật ký hoạt động thành công",
                response
        ));
    }
}
