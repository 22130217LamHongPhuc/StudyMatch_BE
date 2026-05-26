package com.group_service.dto;

import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.StudySessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateStudySessionRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    private String description;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private GroupStudySessionMode studyMode;

    @Size(max = 255)
    private String location;

    @NotNull
    private Long createdByUserId;

    private StudySessionType sessionType;

    private String subjectName;
}

