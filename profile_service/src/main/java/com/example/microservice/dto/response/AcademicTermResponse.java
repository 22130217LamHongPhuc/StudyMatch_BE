package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcademicTermResponse {
    private Long termId;
    private Short academicYearStart;
    private Short academicYearEnd;
    private Byte semesterNo;
    private String fullName;
    private String status;

    public AcademicTermResponse() {
    }

    public AcademicTermResponse(Long termId, int academicYearStart, int academicYearEnd,
                               Byte semesterNo, String fullName, String status) {
        this.termId = termId;
        this.academicYearStart = (short) academicYearStart;
        this.academicYearEnd = (short) academicYearEnd;
        this.semesterNo = semesterNo;
        this.fullName = fullName;
        this.status = status;
    }

}

