package com.example.microservice.dto.respone;

import com.example.microservice.enums.ReportReason;
import com.example.microservice.enums.ReportStatus;
import com.example.microservice.enums.ReportTargetType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponse {

    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("reporter_user_id")
    private Long reporterUserId;

    @JsonProperty("reporter_name")
    private String reporterName;

    @JsonProperty("target_type")
    private ReportTargetType targetType;

    @JsonProperty("target_id")
    private Long targetId;

    @JsonProperty("target_name")
    private String targetName;

    private ReportReason reason;

    private String description;

    private ReportStatus status;

    @JsonProperty("admin_note")
    private String adminNote;

    @JsonProperty("handled_by")
    private Long handledBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
