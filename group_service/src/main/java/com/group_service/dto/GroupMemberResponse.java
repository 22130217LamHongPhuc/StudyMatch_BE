package com.group_service.dto;

import com.group_service.entity.enums.GroupMemberRole;
import com.group_service.entity.enums.GroupMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupMemberResponse {
    private Long userId;
    private GroupMemberRole role;
    private GroupMemberStatus status;
    private LocalDateTime joinedAt;
}
