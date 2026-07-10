package com.group_service.dto;

import com.group_service.entity.enums.GroupInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupInvitationResponse {
    private Long invitationId;
    private Long groupId;
    private String groupName;
    private String groupAvatarUrl;
    private Long inviterUserId;
    private Long inviteeUserId;
    private String inviterName;
    private String inviterAvatar;
    private String message;
    private GroupInvitationStatus status;
    private LocalDateTime createdAt;
}
