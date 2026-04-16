package com.example.microservice.dto.response;

public class AcademicTermResponse {
    private Long termId;
    private Short academicYearStart;
    private Short academicYearEnd;
    private Byte semesterNo;
    private String fullName;
    private String status;

    public AcademicTermResponse() {
    }

    public AcademicTermResponse(Long termId, Short academicYearStart, Short academicYearEnd,
                               Byte semesterNo, String fullName, String status) {
        this.termId = termId;
        this.academicYearStart = academicYearStart;
        this.academicYearEnd = academicYearEnd;
        this.semesterNo = semesterNo;
        this.fullName = fullName;
        this.status = status;
    }

    // Getters and Setters
    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Short getAcademicYearStart() {
        return academicYearStart;
    }

    public void setAcademicYearStart(Short academicYearStart) {
        this.academicYearStart = academicYearStart;
    }

    public Short getAcademicYearEnd() {
        return academicYearEnd;
    }

    public void setAcademicYearEnd(Short academicYearEnd) {
        this.academicYearEnd = academicYearEnd;
    }

    public Byte getSemesterNo() {
        return semesterNo;
    }

    public void setSemesterNo(Byte semesterNo) {
        this.semesterNo = semesterNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

