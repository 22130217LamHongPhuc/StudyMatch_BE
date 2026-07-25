package com.example.microservice.services.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentRejectRequest {

    @NotBlank(message = "Lý do từ chối không được để trống")
    private String rejectionReason;
}
