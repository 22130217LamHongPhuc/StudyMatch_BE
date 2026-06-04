package com.group_service.controller;

import com.group_service.dto.AdminSessionStatsResponse;
import com.group_service.dto.AdminStudySessionResponse;
import com.group_service.dto.ApiResponse;
import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionType;
import com.group_service.enums.StatusCode;
import com.group_service.service.AdminStudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/admin/sessions")
@RequiredArgsConstructor
public class AdminStudySessionController {

    private final AdminStudySessionService adminStudySessionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminStudySessionResponse>>> getSessionsForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) GroupStudySessionStatus status,
            @RequestParam(required = false) GroupStudySessionMode studyMode,
            @RequestParam(required = false) StudySessionType sessionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page<AdminStudySessionResponse> response = adminStudySessionService.getSessionsForAdmin(
                keyword, status, studyMode, sessionType, startFrom, startTo, page, limit
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get sessions successfully",
                response
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminSessionStatsResponse>> getSessionStats() {
        AdminSessionStatsResponse response = adminStudySessionService.getSessionStats();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get session stats successfully",
                response
        ));
    }
}

