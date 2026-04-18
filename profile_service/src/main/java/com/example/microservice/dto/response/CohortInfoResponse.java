package com.example.microservice.dto.response;

import lombok.Getter;

@Getter
public class CohortInfoResponse {
    private Long cohortId;
    private String cohortCode;
    private Integer startAcademicYear;
    private Byte totalStudyYears;
    private CurriculumInfoResponse curriculum;

    public CohortInfoResponse() {
    }

    public CohortInfoResponse(Long cohortId, String cohortCode, Integer startAcademicYear, Byte totalStudyYears) {
        this.cohortId = cohortId;
        this.cohortCode = cohortCode;
        this.startAcademicYear = startAcademicYear;
        this.totalStudyYears = totalStudyYears;
    }

    public void setCohortId(Long cohortId) {
        this.cohortId = cohortId;
    }

    public void setCohortCode(String cohortCode) {
        this.cohortCode = cohortCode;
    }

    public void setStartAcademicYear(Integer startAcademicYear) {
        this.startAcademicYear = startAcademicYear;
    }

    public void setTotalStudyYears(Byte totalStudyYears) {
        this.totalStudyYears = totalStudyYears;
    }

    public void setCurriculum(CurriculumInfoResponse curriculum) {
        this.curriculum = curriculum;
    }
}

