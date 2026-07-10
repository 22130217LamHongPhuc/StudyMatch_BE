package com.example.microservice.service;

import com.example.microservice.dto.request.CreateReportRequest;
import com.example.microservice.dto.request.UpdateReportStatusRequest;
import com.example.microservice.dto.respone.ReportMapper;
import com.example.microservice.dto.respone.ReportResponse;
import com.example.microservice.entity.Report;
import com.example.microservice.enums.ReportReason;
import com.example.microservice.enums.ReportStatus;
import com.example.microservice.enums.ReportTargetType;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.feignAPI.GroupClient;
import com.example.microservice.feignAPI.PostClient;
import com.example.microservice.repository.ReportRepository;
import com.example.microservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {

    ReportRepository reportRepository;
    ReportMapper reportMapper;
    UserRepository userRepository;
    PostClient postClient;
    GroupClient groupClient;

    @Override
    @Transactional
    public ReportResponse createReport(Long reporterUserId, CreateReportRequest request) {
        if (request.getTargetType() == ReportTargetType.USER
                && reporterUserId.equals(request.getTargetId())) {
            throw new AppException("Bạn không thể báo cáo chính mình", StatusCode.SELF_REPORT_NOT_ALLOWED);
        }

        if (reportRepository.existsByReporterUserIdAndTargetTypeAndTargetId(
                reporterUserId, request.getTargetType(), request.getTargetId())) {
            throw new AppException("Bạn đã báo cáo đối tượng này trước đó", StatusCode.DUPLICATE_REPORT);
        }

        validateTargetExists(request.getTargetType(), request.getTargetId());

        Report report = Report.builder()
                .reporterUserId(reporterUserId)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .description(request.getDescription())
                .status(ReportStatus.PENDING)
                .build();

        Report saved = reportRepository.save(report);
        return reportMapper.toResponse(saved);
    }

    @Override
    public Page<ReportResponse> getMyReports(Long reporterUserId, Pageable pageable) {
        return reportRepository
                .findByReporterUserIdOrderByCreatedAtDesc(reporterUserId, pageable)
                .map(reportMapper::toResponse);
    }

    @Override
    public ReportResponse getMyReportDetail(Long reporterUserId, Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException("Không tìm thấy báo cáo", StatusCode.REPORT_NOT_FOUND));

        if (!report.getReporterUserId().equals(reporterUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem báo cáo này");
        }

        return reportMapper.toResponse(report);
    }

    private void validateTargetExists(ReportTargetType targetType, Long targetId) {
        switch (targetType) {
            case USER -> validateUserExists(targetId);
            case POST -> validatePostExists(targetId);
            case GROUP -> validateGroupExists(targetId);
        }
    }

    private void validateUserExists(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new AppException("Người dùng không tồn tại", StatusCode.USER_NOT_FOUND);
        }
    }

    private void validatePostExists(Long postId) {
        try {
            boolean exists = postClient.existsById(postId);
            if (!exists) {
                throw new AppException("Bài viết không tồn tại", StatusCode.TARGET_NOT_FOUND);
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Không thể xác minh bài viết tồn tại", StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateGroupExists(Long groupId) {
        try {
            boolean exists = groupClient.existsById(groupId);
            if (!exists) {
                throw new AppException("Nhóm học không tồn tại", StatusCode.TARGET_NOT_FOUND);
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Không thể xác minh nhóm học tồn tại", StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<ReportResponse> getAllReports(ReportStatus status, ReportTargetType targetType, ReportReason reason, Pageable pageable) {
        return reportRepository
                .findForAdmin(status, targetType, reason, pageable)
                .map(reportMapper::toResponse);
    }

    @Override
    public ReportResponse getReportDetailForAdmin(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException("Không tìm thấy báo cáo", StatusCode.REPORT_NOT_FOUND));
        return reportMapper.toResponse(report);
    }

    @Override
    @Transactional
    public ReportResponse updateReportStatus(Long reportId, Long adminId, UpdateReportStatusRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException("Không tìm thấy báo cáo", StatusCode.REPORT_NOT_FOUND));

        report.setStatus(request.getStatus());
        report.setAdminNote(request.getAdminNote());
        report.setHandledBy(adminId);
        report.setUpdatedAt(LocalDateTime.now());

        Report saved = reportRepository.save(report);
        return reportMapper.toResponse(saved);
    }
}

