package com.example.microservice.dto.response;

public class CurriculumInfoResponse {
    private Long curriculumId;
    private String curriculumCode;
    private String curriculumName;

    public CurriculumInfoResponse() {
    }

    public CurriculumInfoResponse(Long curriculumId, String curriculumCode, String curriculumName) {
        this.curriculumId = curriculumId;
        this.curriculumCode = curriculumCode;
        this.curriculumName = curriculumName;
    }

    // Getters and Setters
    public Long getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(Long curriculumId) {
        this.curriculumId = curriculumId;
    }

    public String getCurriculumCode() {
        return curriculumCode;
    }

    public void setCurriculumCode(String curriculumCode) {
        this.curriculumCode = curriculumCode;
    }

    public String getCurriculumName() {
        return curriculumName;
    }

    public void setCurriculumName(String curriculumName) {
        this.curriculumName = curriculumName;
    }
}

