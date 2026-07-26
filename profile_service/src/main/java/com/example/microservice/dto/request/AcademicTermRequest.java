package com.example.microservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcademicTermRequest {
    @NotNull(message = "Năm học bắt đầu không được để trống")
    private Short academicYearStart;

    @NotNull(message = "Năm học kết thúc không được để trống")
    private Short academicYearEnd;

    @NotNull(message = "Học kỳ không được để trống")
    @Min(value = 1, message = "Học kỳ phải >= 1")
    @Max(value = 3, message = "Học kỳ phải <= 3")
    private Byte semesterNo;

    @NotBlank(message = "Tên học kỳ không được để trống")
    private String fullName;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // planned, active, completed

    public AcademicTermRequest() {
    }
}
