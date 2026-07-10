package com.example.microservice.dto.respone;

import com.example.microservice.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    /**
     * Chuyển đổi entity Report sang ReportResponse DTO.
     * Không chứa logic nghiệp vụ, chỉ ánh xạ thuần túy.
     */
    public ReportResponse toResponse(Report report) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporterUserId(report.getReporterUserId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .adminNote(report.getAdminNote())
                .handledBy(report.getHandledBy())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
