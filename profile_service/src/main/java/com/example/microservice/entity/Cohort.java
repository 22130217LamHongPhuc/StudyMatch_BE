package com.example.microservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "cohorts")
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cohort_id")
    private Long cohortId;

    @Column(name = "cohort_code", length = 20)
    private String cohortCode;

    @Column(name = "start_academic_year")
    private Integer startAcademicYear;

    @Column(name = "total_study_years", nullable = false)
    private Byte totalStudyYears = 4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    @JsonIgnore
    private Curriculum curriculum;

    public Cohort() {
    }

}

