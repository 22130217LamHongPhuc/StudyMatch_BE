package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}

