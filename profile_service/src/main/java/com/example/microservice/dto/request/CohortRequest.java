package com.example.microservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CohortRequest {
    @NotBlank(message = "Mã khóa học không được để trống")
    private String cohortCode;

    @NotNull(message = "Năm bắt đầu học không được để trống")
    private Integer startAcademicYear;

    private Integer totalStudyYears;

    @NotNull(message = "ID chương trình đào tạo không được để trống")
    private Long curriculumId;

    public CohortRequest() {
    }
}
