package com.group_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class StudyGroupDetailResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerUserId;
    private Long termId;
    private Long mainSubjectId;
    private String subjectName;
    private String studyGoal;
    private String studyMode;
    private Integer maxMembers;
    private String visibility;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FreeTimeSlotResponse> freeTimeSlots;

    public StudyGroupDetailResponse() {
    }

    public StudyGroupDetailResponse(
            Long id,
            String name,
            String description,
            Long ownerUserId,
            Long termId,
            Long mainSubjectId,
            String subjectName,
            String studyGoal,
            String studyMode,
            Integer maxMembers,
            String visibility,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<FreeTimeSlotResponse> freeTimeSlots
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerUserId = ownerUserId;
        this.termId = termId;
        this.mainSubjectId = mainSubjectId;
        this.subjectName = subjectName;
        this.studyGoal = studyGoal;
        this.studyMode = studyMode;
        this.maxMembers = maxMembers;
        this.visibility = visibility;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.freeTimeSlots = freeTimeSlots;
    }

}

