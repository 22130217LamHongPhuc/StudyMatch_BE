package com.example.microservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubjectRequest {
    @NotBlank(message = "Mã môn học không được để trống")
    private String subjectCode;

    @NotBlank(message = "Tên môn học không được để trống")
    private String subjectName;

    public SubjectRequest() {
    }
}
