package com.example.microservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

@Setter
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

    public int getAcademicYearStart() {
        return academicYearStart != null ? academicYearStart : 0;
    }

    public int getAcademicYearEnd() {
        return academicYearEnd != null ? academicYearEnd : 0;
    }

    public Byte getSemesterNo() {
        return semesterNo;
    }

    public String getFullName() {
        return fullName;
    }

    public String getStatus() {
        return status;
    }

}
