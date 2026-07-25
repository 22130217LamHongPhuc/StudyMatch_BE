package com.example.microservice.service;

import com.example.microservice.dto.respone.AdminOverviewResponse;
import com.example.microservice.entity.Report;
import com.example.microservice.entity.User;
import com.example.microservice.enums.ReportStatus;
import com.example.microservice.enums.ReportTargetType;
import com.example.microservice.feignAPI.ChatClient;
import com.example.microservice.feignAPI.GroupClient;
import com.example.microservice.repository.ReportRepository;
import com.example.microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminOverviewServiceImpl implements AdminOverviewService {

    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ChatClient chatClient;
    private final GroupClient groupClient;

    @Override
    public AdminOverviewResponse getAdminOverview(String timePreset, String startDate, String endDate) {
        DateRange range = resolveRange(timePreset, startDate, endDate);
        List<Report> reports = reportRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                range.start(), range.endExclusive());

        long pendingReports = countByStatus(reports, ReportStatus.PENDING);
        List<AdminOverviewResponse.ReportStatusStatDto> reportsPie = Arrays.stream(ReportStatus.values())
                .map(status -> new AdminOverviewResponse.ReportStatusStatDto(
                        statusLabel(status), countByStatus(reports, status), statusColor(status)))
                .toList();

        List<AdminOverviewResponse.ReportTargetStatDto> reportsByTarget = Arrays.stream(ReportTargetType.values())
                .map(type -> buildTargetStat(type, reports))
                .toList();

        List<User> usersInRange = userRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                range.start(), range.endExclusive());

        return AdminOverviewResponse.builder()
                .totalUsers(userRepository.count())
                .onlineUsers(chatClient.getOnlineUsersCount())
                .pendingReportsCount(pendingReports)
                .topSubjects(groupClient.getTopSubjects())
                .reportsPie(reportsPie)
                .reportsByTarget(reportsByTarget)
                .messagesTimeline(chatClient.getMessagesTimeline(
                        range.start().toString(), range.endExclusive().toString()))
                .newUsersTimeline(buildNewUsersTimeline(usersInRange))
                .studyDurationTimeline(groupClient.getStudyDurationTimeline(
                        range.start().toString(), range.endExclusive().toString()))
                .build();
    }

    private List<AdminOverviewResponse.NewUsersTimelineDto> buildNewUsersTimeline(List<User> users) {
        Map<LocalDate, Long> counts = new LinkedHashMap<>();
        users.stream()
                .filter(user -> user.getCreatedAt() != null)
                .forEach(user -> counts.merge(user.getCreatedAt().toLocalDate(), 1L, Long::sum));
        return counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AdminOverviewResponse.NewUsersTimelineDto(
                        entry.getKey().format(DATE_LABEL), entry.getValue()))
                .toList();
    }

    private AdminOverviewResponse.ReportTargetStatDto buildTargetStat(
            ReportTargetType type, List<Report> reports) {
        List<Report> targetReports = reports.stream()
                .filter(report -> report.getTargetType() == type)
                .toList();
        long pending = countByStatus(targetReports, ReportStatus.PENDING);
        long reviewing = countByStatus(targetReports, ReportStatus.REVIEWING);
        long resolved = countByStatus(targetReports, ReportStatus.RESOLVED);
        long rejected = countByStatus(targetReports, ReportStatus.REJECTED);
        return new AdminOverviewResponse.ReportTargetStatDto(
                targetLabel(type), pending, reviewing, resolved, rejected, targetReports.size());
    }

    private long countByStatus(List<Report> reports, ReportStatus status) {
        return reports.stream().filter(report -> report.getStatus() == status).count();
    }

    private DateRange resolveRange(String preset, String customStart, String customEnd) {
        LocalDate today = LocalDate.now();
        String normalized = preset == null ? "THIS_WEEK" : preset.toUpperCase();
        return switch (normalized) {
            case "THIS_MONTH" ->
                new DateRange(today.withDayOfMonth(1).atStartOfDay(), today.plusDays(1).atStartOfDay());
            case "ALL_TIME" -> new DateRange(LocalDate.of(1970, 1, 1).atStartOfDay(), today.plusDays(1).atStartOfDay());
            case "CUSTOM" -> {
                if (customStart == null || customEnd == null) {
                    throw new IllegalArgumentException("startDate và endDate là bắt buộc khi timePreset=CUSTOM");
                }
                LocalDate start = LocalDate.parse(customStart);
                LocalDate end = LocalDate.parse(customEnd);
                if (end.isBefore(start)) {
                    throw new IllegalArgumentException("endDate không được nhỏ hơn startDate");
                }
                yield new DateRange(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
            }
            default -> new DateRange(
                    today.with(DayOfWeek.MONDAY).atStartOfDay(),
                    today.plusDays(1).atStartOfDay());
        };
    }

    private String statusLabel(ReportStatus status) {
        return switch (status) {
            case PENDING -> "Đang chờ (PENDING)";
            case REVIEWING -> "Đang xem xét (REVIEWING)";
            case RESOLVED -> "Đã xử lý (RESOLVED)";
            case REJECTED -> "Từ chối (REJECTED)";
        };
    }

    private String statusColor(ReportStatus status) {
        return switch (status) {
            case PENDING -> "#d97706";
            case REVIEWING -> "#2563eb";
            case RESOLVED -> "#059669";
            case REJECTED -> "#dc2626";
        };
    }

    private String targetLabel(ReportTargetType type) {
        return switch (type) {
            case USER -> "User";
            case GROUP -> "Group";
            case POST -> "Post";
            case DOCUMENT -> "Document";
        };
    }

    private record DateRange(LocalDateTime start, LocalDateTime endExclusive) {
    }
}
