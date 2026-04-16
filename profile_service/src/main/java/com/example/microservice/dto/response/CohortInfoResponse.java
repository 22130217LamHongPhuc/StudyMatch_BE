package com.example.microservice.dto.response;

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

    // Getters and Setters
    public Long getCohortId() {
        return cohortId;
    }

    public void setCohortId(Long cohortId) {
        this.cohortId = cohortId;
    }

    public String getCohortCode() {
        return cohortCode;
    }

    public void setCohortCode(String cohortCode) {
        this.cohortCode = cohortCode;
    }

    public Integer getStartAcademicYear() {
        return startAcademicYear;
    }

    public void setStartAcademicYear(Integer startAcademicYear) {
        this.startAcademicYear = startAcademicYear;
    }

    public Byte getTotalStudyYears() {
        return totalStudyYears;
    }

    public void setTotalStudyYears(Byte totalStudyYears) {
        this.totalStudyYears = totalStudyYears;
    }

    public CurriculumInfoResponse getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CurriculumInfoResponse curriculum) {
        this.curriculum = curriculum;
    }
}

