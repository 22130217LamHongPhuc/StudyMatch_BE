package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubjectInfoResponse {
    // Getters and Setters
    private Long subjectId;
    private String subjectCode;
    private String subjectName;

    public SubjectInfoResponse() {
    }

    public SubjectInfoResponse(Long subjectId, String subjectCode, String subjectName) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

}

