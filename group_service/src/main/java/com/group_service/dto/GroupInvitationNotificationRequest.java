package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupInvitationNotificationRequest {
    private Long userId;
    private Long groupId;
    private String groupName;
    private String inviterName;
    private Long invitationId;
    private Long inviteeUserId;
    private String status;
}
