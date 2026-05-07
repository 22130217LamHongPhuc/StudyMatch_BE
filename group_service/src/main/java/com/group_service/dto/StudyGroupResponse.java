package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StudyGroupResponse {

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
}