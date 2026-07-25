package com.example.microservice.services.Dto;

import com.example.microservice.services.entity.DocumentReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDocumentReportRequest {

    @NotNull(message = "Lý do báo cáo không được để trống")
    private DocumentReportReason reason;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;
}
