package com.example.microservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CurriculumSubjectRequest {
    @NotNull(message = "Năm học không được để trống")
    @Min(value = 1, message = "Năm học phải >= 1")
    @Max(value = 10, message = "Năm học không hợp lệ")
    private Integer studyYearNo;

    @NotNull(message = "Học kỳ không được để trống")
    @Min(value = 1, message = "Học kỳ phải >= 1")
    @Max(value = 3, message = "Học kỳ không hợp lệ")
    private Integer semesterNo;

    @NotNull(message = "Mã môn học (subjectId) không được để trống")
    private Long subjectId;

    private Boolean isRequired;

    private Integer recommendedOrder;

    public CurriculumSubjectRequest() {
    }
}
