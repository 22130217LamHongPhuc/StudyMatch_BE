package com.example.microservice.dto.request;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OnboardingSubmitRequest {

    // Getters and Setters
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

