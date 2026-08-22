package com.example.microservice.services;

import com.example.microservice.dto.AdminGroupDetailResponse;
import com.example.microservice.dto.AdminChatDashboardResponse;
import com.example.microservice.dto.AdminApiResponse;
import com.example.microservice.dto.AdminChatUserViolationResponse;
import com.example.microservice.dto.AdminUserSummary;
import com.example.microservice.dto.GroupApiResponse;
import com.example.microservice.dto.PageResponse;
import com.example.microservice.feignClient.GroupClient;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminChatDashboardService {
    private static final DateTimeFormatter TREND_LABEL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM");

    private final MessageRepo messageRepo;
    private final GroupClient groupClient;
    private final UserClient userClient;

    public AdminChatDashboardResponse getDashboard(int groupLimit, int memberLimit) {
        Object[] summaryRow = unwrapSingleRow(messageRepo.findGroupModerationSummary());
        Map<Long, AdminGroupDetailResponse> groupDetailCache = new HashMap<>();
        List<AdminChatDashboardResponse.GroupRisk> topGroups = messageRepo.findGroupModerationRisks(groupLimit)
                .stream()
                .map(row -> toGroupRisk(row, groupDetailCache))
                .toList();
        List<Object[]> memberRows = messageRepo.findMemberModerationRisks(memberLimit);
        Map<Long, AdminUserSummary> userCache = fetchUsersByIds(memberRows.stream()
                .map(row -> toLong(row[1]))
                .collect(Collectors.toSet()));
        List<AdminChatDashboardResponse.MemberRisk> topMembers = memberRows
                .stream()
                .map(row -> toMemberRisk(row, groupDetailCache, userCache))
                .toList();
        List<AdminChatDashboardResponse.ReviewItem> reviewQueue = topMembers.stream()
                .limit(5)
                .map(this::toReviewItem)
                .toList();
        List<AdminChatDashboardResponse.TrendPoint> trend = messageRepo.findGroupModerationTrend()
                .stream()
                .map(this::toTrendPoint)
                .toList();

        return new AdminChatDashboardResponse(
                toSummary(summaryRow),
                topGroups,
                topMembers,
                reviewQueue,
                trend
        );
    }

    public AdminChatUserViolationResponse searchUserViolations(String keyword, int limit) {
        List<AdminUserSummary> users = searchUsers(keyword, limit);
        Map<Long, AdminGroupDetailResponse> groupDetailCache = new HashMap<>();
        List<AdminChatUserViolationResponse.UserViolation> results = users.stream()
                .filter(user -> user.getUserId() != null)
                .map(user -> toUserViolation(user, groupDetailCache))
                .toList();

        return new AdminChatUserViolationResponse(results);
    }

    public void kickUserFromGroup(Long userId, Long groupId) {
        groupClient.kickMember(groupId, userId, Map.of("status", "remove"));
    }

    private AdminChatDashboardResponse.Summary toSummary(Object[] row) {
        if (row == null) {
            return new AdminChatDashboardResponse.Summary(0, 0, 0, 0, 0, 0);
        }

        return new AdminChatDashboardResponse.Summary(
                toLong(row[0]),
                toLong(row[1]),
                toLong(row[2]),
                toLong(row[3]),
                toLong(row[4]),
                toLong(row[5])
        );
    }

    private AdminChatDashboardResponse.GroupRisk toGroupRisk(
            Object[] row,
            Map<Long, AdminGroupDetailResponse> groupDetailCache
    ) {
        long groupId = toLong(row[0]);
        long conversationId = toLong(row[1]);
        AdminGroupDetailResponse groupDetail = getGroupDetail(groupId, groupDetailCache);

        return new AdminChatDashboardResponse.GroupRisk(
                groupId,
                resolveGroupName(groupId, groupDetail),
                conversationId,
                toLong(row[2]),
                toLong(row[3]),
                toLong(row[4]),
                toLong(row[5]),
                groupDetail != null && groupDetail.getMemberCount() != null ? groupDetail.getMemberCount() : 0,
                toIsoDateTime(row[6])
        );
    }

    private AdminChatDashboardResponse.MemberRisk toMemberRisk(
            Object[] row,
            Map<Long, AdminGroupDetailResponse> groupDetailCache,
            Map<Long, AdminUserSummary> userCache
    ) {
        long groupId = toLong(row[0]);
        long senderId = toLong(row[1]);
        AdminGroupDetailResponse groupDetail = getGroupDetail(groupId, groupDetailCache);
        AdminUserSummary user = userCache.get(senderId);

        return new AdminChatDashboardResponse.MemberRisk(
                groupId,
                resolveGroupName(groupId, groupDetail),
                senderId,
                resolveUserName(senderId, user),
                user != null ? user.getAvatarUrl() : null,
                user != null ? user.getEmail() : null,
                toLong(row[2]),
                toLong(row[3]),
                toLong(row[4]),
                toIsoDateTime(row[5])
        );
    }

    private AdminChatUserViolationResponse.UserViolation toUserViolation(
            AdminUserSummary user,
            Map<Long, AdminGroupDetailResponse> groupDetailCache
    ) {
        List<AdminChatUserViolationResponse.GroupViolation> groups = messageRepo
                .findUserGroupModerationRisks(user.getUserId())
                .stream()
                .map(row -> toGroupViolation(row, groupDetailCache))
                .toList();
        long totalMessages = groups.stream()
                .mapToLong(AdminChatUserViolationResponse.GroupViolation::totalMessages)
                .sum();
        long offensiveMessages = groups.stream()
                .mapToLong(AdminChatUserViolationResponse.GroupViolation::offensiveMessages)
                .sum();
        long hateMessages = groups.stream()
                .mapToLong(AdminChatUserViolationResponse.GroupViolation::hateMessages)
                .sum();

        return new AdminChatUserViolationResponse.UserViolation(
                user.getUserId(),
                resolveUserName(user.getUserId(), user),
                user.getAvatarUrl(),
                user.getEmail(),
                countJoinedGroups(user.getUserId(), groups.size()),
                totalMessages,
                offensiveMessages,
                hateMessages,
                groups
        );
    }

    private AdminChatUserViolationResponse.GroupViolation toGroupViolation(
            Object[] row,
            Map<Long, AdminGroupDetailResponse> groupDetailCache
    ) {
        long groupId = toLong(row[0]);
        AdminGroupDetailResponse groupDetail = getGroupDetail(groupId, groupDetailCache);

        return new AdminChatUserViolationResponse.GroupViolation(
                groupId,
                resolveGroupName(groupId, groupDetail),
                toLong(row[1]),
                toLong(row[2]),
                toLong(row[3]),
                toLong(row[4]),
                toIsoDateTime(row[5])
        );
    }

    private AdminGroupDetailResponse getGroupDetail(
            Long groupId,
            Map<Long, AdminGroupDetailResponse> groupDetailCache
    ) {
        if (groupId == null) {
            return null;
        }

        if (groupDetailCache.containsKey(groupId)) {
            return groupDetailCache.get(groupId);
        }

        try {
            GroupApiResponse<AdminGroupDetailResponse> response = groupClient.getAdminGroupDetail(groupId);
            AdminGroupDetailResponse detail = response != null && response.isSuccess() ? response.getData() : null;
            groupDetailCache.put(groupId, detail);
            return detail;
        } catch (Exception ex) {
            groupDetailCache.put(groupId, null);
            return null;
        }
    }

    private String resolveGroupName(Long groupId, AdminGroupDetailResponse groupDetail) {
        if (groupDetail != null && groupDetail.getName() != null && !groupDetail.getName().isBlank()) {
            return groupDetail.getName();
        }

        return "Nh\u00f3m #" + groupId;
    }

    private long countJoinedGroups(Long userId, int fallback) {
        try {
            GroupApiResponse<List<AdminGroupDetailResponse>> response = groupClient.getUserGroups(userId);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData().size();
            }
        } catch (Exception ignored) {
        }

        return fallback;
    }

    private String resolveUserName(Long userId, AdminUserSummary user) {
        if (user != null && user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName();
        }

        return "User #" + userId;
    }

    private Map<Long, AdminUserSummary> fetchUsersByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        try {
            AdminApiResponse<List<AdminUserSummary>> response = userClient.getBasicUsers(new ArrayList<>(userIds));
            if (response == null || !response.isSuccess() || response.getData() == null) {
                return Map.of();
            }

            return response.getData().stream()
                    .filter(Objects::nonNull)
                    .filter(user -> user.getUserId() != null)
                    .collect(Collectors.toMap(
                            AdminUserSummary::getUserId,
                            user -> user,
                            (first, second) -> first
                    ));
        } catch (Exception ex) {
            return Map.of();
        }
    }

    private List<AdminUserSummary> searchUsers(String keyword, int limit) {
        Map<Long, AdminUserSummary> users = new LinkedHashMap<>();
        Long numericUserId = parseUserId(keyword);

        if (numericUserId != null) {
            users.putAll(fetchUsersByIds(Set.of(numericUserId)));
        }

        try {
            AdminApiResponse<PageResponse<AdminUserSummary>> response = userClient.searchAdminUsers(
                    0,
                    limit,
                    keyword == null ? "" : keyword.trim()
            );
            PageResponse<AdminUserSummary> page = response != null && response.isSuccess() ? response.getData() : null;
            if (page != null && page.getContent() != null) {
                page.getContent().stream()
                        .filter(Objects::nonNull)
                        .filter(user -> user.getUserId() != null)
                        .forEach(user -> users.putIfAbsent(user.getUserId(), user));
            }
        } catch (Exception ignored) {
        }

        return users.values().stream().limit(limit).toList();
    }

    private Long parseUserId(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(keyword.trim().replaceFirst("^#", ""));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private AdminChatDashboardResponse.ReviewItem toReviewItem(AdminChatDashboardResponse.MemberRisk member) {
        boolean shouldKick = member.hateMessages() >= 3;
        boolean shouldMute = !shouldKick && (member.hateMessages() > 0 || member.offensiveMessages() >= 5);
        String priority = shouldKick || member.hateMessages() > 0 ? "HIGH" : "MEDIUM";
        String suggestion = shouldKick
                ? "\u0110\u1ec1 xu\u1ea5t kick kh\u1ecfi nh\u00f3m"
                : shouldMute ? "\u0110\u1ec1 xu\u1ea5t mute 24 gi\u1edd v\u00e0 review" : "C\u1ea3nh b\u00e1o l\u1ea7n 1";
        String reason = "%d tin HATE v\u00e0 %d tin OFFENSIVE".formatted(
                member.hateMessages(),
                member.offensiveMessages()
        );

        return new AdminChatDashboardResponse.ReviewItem(
                "case-%d-%d".formatted(member.senderId(), member.groupId()),
                priority,
                member.groupName(),
                member.senderName(),
                reason,
                suggestion
        );
    }

    private AdminChatDashboardResponse.TrendPoint toTrendPoint(Object[] row) {
        return new AdminChatDashboardResponse.TrendPoint(
                toTrendLabel(row[0]),
                toLong(row[1]),
                toLong(row[2])
        );
    }

    private Object[] unwrapSingleRow(Object row) {
        if (row instanceof Object[] values && values.length == 1 && values[0] instanceof Object[] nested) {
            return nested;
        }

        if (row instanceof Object[] values) {
            return values;
        }

        return null;
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.parseLong(value.toString());
    }

    private String toIsoDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toString();
        }

        return value.toString();
    }

    private String toTrendLabel(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Date date) {
            return date.toLocalDate().format(TREND_LABEL_FORMATTER);
        }

        if (value instanceof java.util.Date date) {
            return new Date(date.getTime()).toLocalDate().format(TREND_LABEL_FORMATTER);
        }

        return LocalDate.parse(value.toString()).format(TREND_LABEL_FORMATTER);
    }
}
