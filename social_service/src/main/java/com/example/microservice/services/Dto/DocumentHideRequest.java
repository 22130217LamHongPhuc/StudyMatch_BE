package com.example.microservice.services.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentHideRequest {

    @NotBlank(message = "Lý do ẩn tài liệu không được để trống")
    private String hiddenReason;
}
