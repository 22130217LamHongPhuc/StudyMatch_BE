package com.example.microservice.dto.response;

import java.math.BigDecimal;

public class StudentTermProfileDetailResponse {
    private Long id;
    private Long userId;
    private AcademicTermResponse term;
    private Byte studyYearNo;
    private Byte semesterNo;
    private BigDecimal avgScore;
    private Integer studiedCredits;
    private String studyGoal;
    private String studyMode;
    private Long mainSubjectId;
    private String mainSubjectName;

    public StudentTermProfileDetailResponse() {
    }

    public StudentTermProfileDetailResponse(Long id, Long userId, Byte studyYearNo, Byte semesterNo,
                                           BigDecimal avgScore, Integer studiedCredits, String studyGoal,
                                           String studyMode, Long mainSubjectId, String mainSubjectName) {
        this.id = id;
        this.userId = userId;
        this.studyYearNo = studyYearNo;
        this.semesterNo = semesterNo;
        this.avgScore = avgScore;
        this.studiedCredits = studiedCredits;
        this.studyGoal = studyGoal;
        this.studyMode = studyMode;
        this.mainSubjectId = mainSubjectId;
        this.mainSubjectName = mainSubjectName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AcademicTermResponse getTerm() {
        return term;
    }

    public void setTerm(AcademicTermResponse term) {
        this.term = term;
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

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    public Integer getStudiedCredits() {
        return studiedCredits;
    }

    public void setStudiedCredits(Integer studiedCredits) {
        this.studiedCredits = studiedCredits;
    }

    public String getStudyGoal() {
        return studyGoal;
    }

    public void setStudyGoal(String studyGoal) {
        this.studyGoal = studyGoal;
    }

    public String getStudyMode() {
        return studyMode;
    }

    public void setStudyMode(String studyMode) {
        this.studyMode = studyMode;
    }

    public Long getMainSubjectId() {
        return mainSubjectId;
    }

    public void setMainSubjectId(Long mainSubjectId) {
        this.mainSubjectId = mainSubjectId;
    }

    public String getMainSubjectName() {
        return mainSubjectName;
    }

    public void setMainSubjectName(String mainSubjectName) {
        this.mainSubjectName = mainSubjectName;
    }
}

