package com.example.microservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "academic_terms")
public class AcademicTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long termId;

    @Column(name = "academic_year_start")
    private Short academicYearStart;

    @Column(name = "academic_year_end")
    private Short academicYearEnd;

    @Column(name = "semester_no")
    private Byte semesterNo;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @Column(name = "status", length = 20)
    private String status;

    public AcademicTerm() {
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public int getAcademicYearStart() {
        return academicYearStart;
    }

    public void setAcademicYearStart(Short academicYearStart) {
        this.academicYearStart = academicYearStart;
    }

    public int getAcademicYearEnd() {
        return academicYearEnd;
    }

    public void setAcademicYearEnd(Short academicYearEnd) {
        this.academicYearEnd = academicYearEnd;
    }

    public int getSemesterNo() {
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

