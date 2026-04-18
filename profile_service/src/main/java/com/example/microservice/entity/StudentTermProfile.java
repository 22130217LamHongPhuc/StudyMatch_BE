package com.example.microservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "student_term_profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_term", columnNames = {"user_id", "term_id"})
})
public class StudentTermProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;

    @Column(name = "study_year_no", nullable = false)
    private Byte studyYearNo;

    @Column(name = "semester_no", nullable = false)
    private Byte semesterNo;

    @Column(name = "avg_score", precision = 4, scale = 2)
    private BigDecimal avgScore;

    @Column(name = "studied_credits")
    private Integer studiedCredits;

    @Column(name = "study_goal", length = 50)
    private String studyGoal;

    @Column(name = "study_mode", length = 50)
    private String studyMode;

    @Column(name = "main_subject_id")
    private Long mainSubjectId;

    public StudentTermProfile() {
    }

}

