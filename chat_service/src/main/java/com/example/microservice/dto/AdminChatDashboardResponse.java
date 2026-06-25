package com.example.microservice.dto;

import java.util.List;

public record AdminChatDashboardResponse(
        Summary summary,
        List<GroupRisk> topGroups,
        List<MemberRisk> topMembers,
        List<ReviewItem> reviewQueue,
        List<TrendPoint> trend
) {
    public record Summary(
            long totalMessages,
            long totalViolations,
            long offensiveMessages,
            long hateMessages,
            long groupsWithViolations,
            long violatingMembers
    ) {
    }

    public record GroupRisk(
            long groupId,
            String groupName,
            long conversationId,
            long totalMessages,
            long offensiveMessages,
            long hateMessages,
            long violatingMembers,
            long activeMembers,
            String lastViolationAt
    ) {
    }

    public record MemberRisk(
            long groupId,
            String groupName,
            long senderId,
            String senderName,
            String senderAvatarUrl,
            String senderEmail,
            long totalMessages,
            long offensiveMessages,
            long hateMessages,
            String lastViolationAt
    ) {
    }

    public record ReviewItem(
            String id,
            String priority,
            String groupName,
            String senderName,
            String reason,
            String suggestion
    ) {
    }

    public record TrendPoint(
            String label,
            long offensive,
            long hate
    ) {
    }
}
