package com.group_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateStudyGroupRequest {

        @NotBlank
        @Size(max = 150)
        private String name;

        private String description;

        @NotNull
        private Long ownerUserId;

        private Long termId;


        @NotNull
        private Long mainSubjectId;

        @Size(max = 150)
        private String subjectName;

        @Min(1)
        private Integer maxMembers;

        @Size(max = 30)
        private String visibility;

        @Size(max = 100)
        private List<@NotNull Long> invitedUserIds;

        private String avatarUrl;

        private List<FreeTimeSlotRequest> freeTimeSlots;
}

