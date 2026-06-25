package com.example.microservice.dto;

import java.util.List;

public record AdminChatUserViolationResponse(
        List<UserViolation> users
) {
    public record UserViolation(
            long userId,
            String fullName,
            String avatarUrl,
            String email,
            long totalJoinedGroups,
            long totalMessages,
            long offensiveMessages,
            long hateMessages,
            List<GroupViolation> groups
    ) {
    }

    public record GroupViolation(
            long groupId,
            String groupName,
            long conversationId,
            long totalMessages,
            long offensiveMessages,
            long hateMessages,
            String lastViolationAt
    ) {
    }
}
