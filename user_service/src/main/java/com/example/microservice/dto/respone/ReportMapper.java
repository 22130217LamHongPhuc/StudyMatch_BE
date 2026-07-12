package com.example.microservice.dto.respone;

import com.example.microservice.entity.Report;
import com.example.microservice.entity.User;
import com.example.microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportMapper {

    private final UserRepository userRepository;

    public ReportResponse toResponse(Report report) {
        String reporterName = userRepository.findById(report.getReporterUserId())
                .map(User::getFullName)
                .orElse("N/A");

        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporterUserId(report.getReporterUserId())
                .reporterName(reporterName)
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
