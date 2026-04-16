package com.example.microservice.dto.response;

import java.time.LocalDateTime;

public class StudentProfileDetailResponse {
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
                                       String gender, String ageGroup, String region, LocalDateTime createdAt) {
        this.profileId = profileId;
        this.userId = userId;
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.region = region;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public CohortInfoResponse getCohort() {
        return cohort;
    }

    public void setCohort(CohortInfoResponse cohort) {
        this.cohort = cohort;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

