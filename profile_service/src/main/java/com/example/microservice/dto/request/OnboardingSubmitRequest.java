package com.example.microservice.dto.request;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OnboardingSubmitRequest {
    
    private String studentCode;
    private String fullName;
    private String gender;
    private String ageGroup;
    private String region;
    private Long cohortId;
    private Long termId;
    private Byte studyYearNo;
    private Byte semesterNo;
    private BigDecimal avgScore;
    private Integer studiedCredits;
    private String studyGoal;
    private String studyMode;
    private Long mainSubjectId;
    
    @JsonProperty("currentSubjectIds")
    private List<Long> currentSubjectIds;
    
    @JsonProperty("freeTimeSlots")
    private List<FreeTimeSlotDto> freeTimeSlots;
    
    @JsonProperty("subjectScheduleSlots")
    private List<SubjectScheduleSlotDto> subjectScheduleSlots;

    // Getters and Setters
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

    public Long getCohortId() {
        return cohortId;
    }

    public void setCohortId(Long cohortId) {
        this.cohortId = cohortId;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
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
        this.semestervnNo = semesterNo;
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

    public List<Long> getCurrentSubjectIds() {
        return currentSubjectIds;
    }

    public void setCurrentSubjectIds(List<Long> currentSubjectIds) {
        this.currentSubjectIds = currentSubjectIds;
    }

    public List<FreeTimeSlotDto> getFreeTimeSlots() {
        return freeTimeSlots;
    }

    public void setFreeTimeSlots(List<FreeTimeSlotDto> freeTimeSlots) {
        this.freeTimeSlots = freeTimeSlots;
    }

    public List<SubjectScheduleSlotDto> getSubjectScheduleSlots() {
        return subjectScheduleSlots;
    }

    public void setSubjectScheduleSlots(List<SubjectScheduleSlotDto> subjectScheduleSlots) {
        this.subjectScheduleSlots = subjectScheduleSlots;
    }

    // Inner DTO Classes
    public static class FreeTimeSlotDto {
        private Byte dayOfWeek;
        private String slotCode;

        public Byte getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(Byte dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public String getSlotCode() {
            return slotCode;
        }

        public void setSlotCode(String slotCode) {
            this.slotCode = slotCode;
        }
    }

    public static class SubjectScheduleSlotDto {
        private Long subjectId;
        private Byte dayOfWeek;
        private String slotCode;
        private String scheduleType;

        public Long getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
        }

        public Byte getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(Byte dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public String getSlotCode() {
            return slotCode;
        }

        public void setSlotCode(String slotCode) {
            this.slotCode = slotCode;
        }

        public String getScheduleType() {
            return scheduleType;
        }

        public void setScheduleType(String scheduleType) {
            this.scheduleType = scheduleType;
        }
    }
}

