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

@Entity
@Table(name = "curriculum_term_subjects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cts", columnNames = {"curriculum_id", "study_year_no", "semester_no", "subject_id"})
})
public class CurriculumTermSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    private Curriculum curriculum;

    @Column(name = "study_year_no", nullable = false)
    private Byte studyYearNo;

    @Column(name = "semester_no", nullable = false)
    private Byte semesterNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "is_required")
    private Boolean required = Boolean.TRUE;

    @Column(name = "recommended_order")
    private Integer recommendedOrder;

    public CurriculumTermSubject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(Curriculum curriculum) {
        this.curriculum = curriculum;
    }

    public Byte getStudyYearNo() {
        return studyYearNo;
    }

    public void setStudyYearNo(Byte studyYearNo) {
        this.studyYearNo = studyYearNo;
    }

    public Byte getSemesterNo() {
        return semesterNo;
    }

    public void setSemesterNo(Byte semesterNo) {
        this.semesterNo = semesterNo;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Integer getRecommendedOrder() {
        return recommendedOrder;
    }

    public void setRecommendedOrder(Integer recommendedOrder) {
        this.recommendedOrder = recommendedOrder;
    }
}

