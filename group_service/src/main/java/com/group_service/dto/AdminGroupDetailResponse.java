package com.group_service.dto;

import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.entity.enums.GroupVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdminGroupDetailResponse {

    private Long id;
    private String name;
    private String avatarUrl;
    private String description;

    private GroupType groupType;

    private Long createdByUserId;
    private Long ownerUserId;

    private Long termId;
    private Long mainSubjectId;
    private String subjectName;

    private Integer maxMembers;
    private GroupVisibility visibility;
    private GroupStatus status;

    private Long memberCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<FreeTimeSlotResponse> freeTimeSlots;
    private List<MemberInfo> members;

    @Getter
    @Setter
    @AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class MemberInfo {
        private Long userId;
        private String fullName;
        private String email;
        private String avatarUrl;
        private String role;
        private String status;
        private LocalDateTime joinedAt;
    }
}

