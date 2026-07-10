package com.example.microservice.dto.request;

import com.example.microservice.enums.ReportReason;
import com.example.microservice.enums.ReportTargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReportRequest {

    @NotNull(message = "targetType không được để trống")
    private ReportTargetType targetType;

    @NotNull(message = "targetId không được để trống")
    private Long targetId;

    @NotNull(message = "reason không được để trống")
    private ReportReason reason;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;
}
