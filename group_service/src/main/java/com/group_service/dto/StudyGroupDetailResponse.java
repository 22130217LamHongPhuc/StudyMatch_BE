package com.group_service.dto;

import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class StudyGroupDetailResponse {

    private Long id;
    private String name;
    private String avatarUrl;
    private String description;
    private Long ownerUserId;
    private Long termId;
    private Long mainSubjectId;
    private String subjectName;

    private Integer maxMembers;
    private GroupVisibility visibility;
    private GroupStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FreeTimeSlotResponse> freeTimeSlots;



}

