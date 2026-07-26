package com.example.microservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CurriculumRequest {
    @NotBlank(message = "Mã chương trình đào tạo không được để trống")
    private String curriculumCode;

    @NotBlank(message = "Tên chương trình đào tạo không được để trống")
    private String curriculumName;

    public CurriculumRequest() {
    }
}
