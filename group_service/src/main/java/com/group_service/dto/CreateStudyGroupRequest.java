package com.group_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStudyGroupRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        String description,

        @NotNull
        Long ownerUserId,

        @NotNull
        Long termId,

        @NotNull
        @Min(1)
        @Max(127)
        Integer studyYearNo,

        @NotNull
        @Min(1)
        @Max(127)
        Integer semesterNo,

        @NotNull
        Long mainSubjectId,

        @Size(max = 150)
        String subjectName,

        @Size(max = 50)
        String studyGoal,

        @Size(max = 50)
        String studyMode,

        @NotNull
        @Min(1)
        Integer maxMembers,

        @Size(max = 30)
        String visibility
) {
}

