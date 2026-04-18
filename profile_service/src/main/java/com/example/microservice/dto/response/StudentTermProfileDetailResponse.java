package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class StudentTermProfileDetailResponse {
    // Getters and Setters
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

}

