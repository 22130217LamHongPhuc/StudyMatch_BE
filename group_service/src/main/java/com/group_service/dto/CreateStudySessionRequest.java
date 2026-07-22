package com.group_service.dto;

import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.StudySessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateStudySessionRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    private String description;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private GroupStudySessionMode studyMode;

    @Size(max = 255)
    private String location;

    @Size(max = 500)
    private String meetingUrl;

    @NotNull
    private Long createdByUserId;

    private StudySessionType sessionType;

    private String subjectName;

    private Long partnerUserId;

    @Size(max = 100)
    private String partnerUserName;

    private Long subjectId;

    private String recurrenceType;

    private List<String> repeatDays;
}
