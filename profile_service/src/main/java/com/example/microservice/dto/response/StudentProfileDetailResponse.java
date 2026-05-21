package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class StudentProfileDetailResponse {
    // Getters and Setters
    private Long profileId;
    private Long userId;
    private String studentCode;
    private String fullName;
    private String gender;
    private String ageGroup;
    private String region;
    private CohortInfoResponse cohort;
    private LocalDateTime createdAt;

    public StudentProfileDetailResponse() {
    }

    public StudentProfileDetailResponse(Long profileId, Long userId, String studentCode, String fullName,
                                       String gender, String ageGroup, String region, LocalDateTime createdAt
                                        ) {
        this.profileId = profileId;
        this.userId = userId;
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.region = region;
        this.createdAt = createdAt;
    }

}

