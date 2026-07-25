package com.example.microservice.service;

import com.example.microservice.dto.request.CreateReportRequest;
import com.example.microservice.dto.request.UpdateReportStatusRequest;
import com.example.microservice.dto.respone.ReportResponse;
import com.example.microservice.enums.ReportReason;
import com.example.microservice.enums.ReportStatus;
import com.example.microservice.enums.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ReportService {

    ReportResponse createReport(Long reporterUserId, CreateReportRequest request);

    Page<ReportResponse> getMyReports(Long reporterUserId, Pageable pageable);

    ReportResponse getMyReportDetail(Long reporterUserId, Long reportId);

    Page<ReportResponse> getAllReports(ReportStatus status, ReportTargetType targetType, ReportReason reason, Pageable pageable);

    ReportResponse getReportDetailForAdmin(Long reportId);

    ReportResponse updateReportStatus(Long reportId, Long adminId, UpdateReportStatusRequest request);

    Map<Long, Long> getUnresolvedReportCounts(ReportTargetType targetType, List<Long> targetIds);
}
