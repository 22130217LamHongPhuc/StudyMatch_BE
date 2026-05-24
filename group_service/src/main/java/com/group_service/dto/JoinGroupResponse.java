package com.group_service.dto;

import com.group_service.entity.enums.GroupMemberRole;
import com.group_service.entity.enums.GroupMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class JoinGroupResponse {

    private Long id;
    private Long groupId;
    private Long userId;
    private GroupMemberRole role;
    private GroupMemberStatus status;
    private LocalDateTime joinedAt;
}

