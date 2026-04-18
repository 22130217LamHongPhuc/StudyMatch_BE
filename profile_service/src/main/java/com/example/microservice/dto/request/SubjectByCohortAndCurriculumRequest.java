package com.example.microservice.dto.request;


    import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
    import lombok.Getter;
    import lombok.Setter;

@Setter
@Getter
public class SubjectByCohortAndCurriculumRequest {



    @NotNull(message = "studyYearNo không được null")
    @Min(value = 1, message = "studyYearNo phải >= 1")
    @Max(value = 10, message = "studyYearNo không hợp lệ")
    private Integer studyYearNo;

    @NotNull(message = "semesterNo không được null")
    @Min(value = 1, message = "semesterNo phải >= 1")
    @Max(value = 3, message = "semesterNo không hợp lệ")
    private Integer semesterNo;

    private Integer startYearTerm;
    private Integer endYearTerm;

    public SubjectByCohortAndCurriculumRequest() {
    }

}