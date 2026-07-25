package com.example.microservice.dto.respone;

import com.example.microservice.entity.Report;
import com.example.microservice.entity.User;
import com.example.microservice.feignAPI.GroupClient;
import com.example.microservice.feignAPI.PostClient;
import com.example.microservice.feignAPI.SocialClient;
import com.example.microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportMapper {

    private final UserRepository userRepository;
    private final PostClient postClient;
    private final GroupClient groupClient;
    private final SocialClient socialClient;

    public ReportResponse toResponse(Report report) {
        String reporterName = userRepository.findById(report.getReporterUserId())
                .map(User::getFullName)
                .orElse("N/A");

        String targetName = "N/A";
        try {
            if (report.getTargetType() != null) {
                switch (report.getTargetType()) {
                    case USER -> {
                        targetName = userRepository.findById(report.getTargetId())
                                .map(User::getFullName)
                                .orElse("Người dùng #" + report.getTargetId());
                    }
                    case DOCUMENT -> {
                        Map<String, Object> docRes = socialClient.getDocumentDetails(report.getTargetId());
                        if (docRes != null && docRes.containsKey("data")) {
                            Map<String, Object> data = (Map<String, Object>) docRes.get("data");
                            if (data != null && data.containsKey("title")) {
                                targetName = (String) data.get("title");
                            }
                        }
                    }
                    case GROUP -> {
                        Map<String, Object> groupRes = groupClient.getGroup(report.getTargetId());
                        if (groupRes != null) {
                            if (groupRes.containsKey("name")) {
                                targetName = (String) groupRes.get("name");
                            } else if (groupRes.containsKey("data")) {
                                Map<String, Object> data = (Map<String, Object>) groupRes.get("data");
                                if (data != null && data.containsKey("name")) {
                                    targetName = (String) data.get("name");
                                }
                            }
                        }
                    }
                    case POST -> {
                        Map<String, Object> postRes = postClient.getPost(report.getTargetId());
                        if (postRes != null && postRes.containsKey("data")) {
                            Map<String, Object> data = (Map<String, Object>) postRes.get("data");
                            if (data != null && data.containsKey("content")) {
                                String content = (String) data.get("content");
                                if (content != null) {
                                    targetName = content.length() > 50 
                                        ? content.substring(0, 47) + "..." 
                                        : content;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch target name: " + e.getMessage());
            targetName = report.getTargetType() + " #" + report.getTargetId();
        }

        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporterUserId(report.getReporterUserId())
                .reporterName(reporterName)
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .targetName(targetName)
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
