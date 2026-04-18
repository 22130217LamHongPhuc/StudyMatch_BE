package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentSubjectEnrollmentResponse {
    // Getters and Setters
    private Long enrollmentId;
    private SubjectInfoResponse subject;
    private AcademicTermResponse term;

    public StudentSubjectEnrollmentResponse() {
    }

    public StudentSubjectEnrollmentResponse(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

}

