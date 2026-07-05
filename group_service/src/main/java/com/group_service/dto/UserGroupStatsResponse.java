package com.group_service.dto;

public record UserGroupStatsResponse(
        long joinedGroupCount,
        long pendingInvitationCount
) {
}
