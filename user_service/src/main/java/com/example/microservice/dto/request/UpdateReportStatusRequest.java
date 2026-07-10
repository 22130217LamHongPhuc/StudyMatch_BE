package com.example.microservice.dto.request;

import com.example.microservice.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReportStatusRequest {

    @NotNull(message = "status không được để trống")
    private ReportStatus status;

    private String adminNote;
}
