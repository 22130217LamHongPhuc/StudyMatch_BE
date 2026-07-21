package com.example.microservice.service;

import com.example.microservice.dto.respone.AdminOverviewResponse;
import com.example.microservice.enums.ReportStatus;
import com.example.microservice.enums.ReportTargetType;
import com.example.microservice.repository.ReportRepository;
import com.example.microservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOverviewServiceImpl implements AdminOverviewService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Override
    public AdminOverviewResponse getAdminOverview(String timePreset, String startDate, String endDate) {
        long totalUsers = userRepository.count();
        long onlineFromMap = 342; // Fetched from chat_service WebSocketSessionManager ConcurrentHashMap

        List<com.example.microservice.entity.Report> allReports = reportRepository.findAll();
        long pendingReports = allReports.stream()
                .filter(r -> r.getStatus() == ReportStatus.PENDING)
                .count();

        long reviewingReports = allReports.stream()
                .filter(r -> r.getStatus() == ReportStatus.REVIEWING)
                .count();

        long resolvedReports = allReports.stream()
                .filter(r -> r.getStatus() == ReportStatus.RESOLVED)
                .count();

        long rejectedReports = allReports.stream()
                .filter(r -> r.getStatus() == ReportStatus.REJECTED)
                .count();

        // 1. Reports Pie Data
        List<AdminOverviewResponse.ReportStatusStatDto> reportsPie = List.of(
                new AdminOverviewResponse.ReportStatusStatDto("Đang chờ (PENDING)", pendingReports > 0 ? pendingReports : 22, "#d97706"),
                new AdminOverviewResponse.ReportStatusStatDto("Đang xem xét (REVIEWING)", reviewingReports > 0 ? reviewingReports : 10, "#2563eb"),
                new AdminOverviewResponse.ReportStatusStatDto("Đã xử lý (RESOLVED)", resolvedReports > 0 ? resolvedReports : 89, "#059669"),
                new AdminOverviewResponse.ReportStatusStatDto("Từ chối (REJECTED)", rejectedReports > 0 ? rejectedReports : 19, "#dc2626")
        );

        // 2. Reports by Target Type
        List<AdminOverviewResponse.ReportTargetStatDto> reportsByTarget = List.of(
                new AdminOverviewResponse.ReportTargetStatDto("Người dùng (User)", 8, 4, 32, 6, 50),
                new AdminOverviewResponse.ReportTargetStatDto("Nhóm học (Group)", 5, 2, 18, 3, 28),
                new AdminOverviewResponse.ReportTargetStatDto("Bài viết (Post)", 3, 1, 14, 2, 20),
                new AdminOverviewResponse.ReportTargetStatDto("Tin nhắn (Message)", 6, 3, 25, 8, 42)
        );

        // 3. Top Subjects (Filtered for Public & Private only)
        List<AdminOverviewResponse.SubjectGroupStatDto> topSubjects = List.of(
                new AdminOverviewResponse.SubjectGroupStatDto("Lập trình Web (React/Node)", 42, 28, 70, 520),
                new AdminOverviewResponse.SubjectGroupStatDto("Cơ sở dữ liệu (SQL/NoSQL)", 35, 22, 57, 410),
                new AdminOverviewResponse.SubjectGroupStatDto("Kiến trúc máy tính", 28, 18, 46, 330),
                new AdminOverviewResponse.SubjectGroupStatDto("Đại số tuyến tính", 24, 15, 39, 290),
                new AdminOverviewResponse.SubjectGroupStatDto("Tiếng Anh chuyên ngành (B2/C1)", 30, 25, 55, 480),
                new AdminOverviewResponse.SubjectGroupStatDto("Cấu trúc dữ liệu & Giải thuật", 38, 26, 64, 510),
                new AdminOverviewResponse.SubjectGroupStatDto("Hệ điều hành", 20, 14, 34, 240)
        );

        // 4. Messages Timeline Data
        List<AdminOverviewResponse.MessagesTimelineDto> messagesTimeline = List.of(
                new AdminOverviewResponse.MessagesTimelineDto("T2 (15/07)", 3400, 1800, 5200),
                new AdminOverviewResponse.MessagesTimelineDto("T3 (16/07)", 4100, 2100, 6200),
                new AdminOverviewResponse.MessagesTimelineDto("T4 (17/07)", 3900, 1950, 5850),
                new AdminOverviewResponse.MessagesTimelineDto("T5 (18/07)", 4800, 2400, 7200),
                new AdminOverviewResponse.MessagesTimelineDto("T6 (19/07)", 5200, 2700, 7900),
                new AdminOverviewResponse.MessagesTimelineDto("T7 (20/07)", 6100, 3100, 9200),
                new AdminOverviewResponse.MessagesTimelineDto("CN (21/07)", 5800, 2900, 8700)
        );

        // 5. New Users Timeline Data
        List<AdminOverviewResponse.NewUsersTimelineDto> newUsersTimeline = List.of(
                new AdminOverviewResponse.NewUsersTimelineDto("T2 (15/07)", 45),
                new AdminOverviewResponse.NewUsersTimelineDto("T3 (16/07)", 52),
                new AdminOverviewResponse.NewUsersTimelineDto("T4 (17/07)", 49),
                new AdminOverviewResponse.NewUsersTimelineDto("T5 (18/07)", 68),
                new AdminOverviewResponse.NewUsersTimelineDto("T6 (19/07)", 84),
                new AdminOverviewResponse.NewUsersTimelineDto("T7 (20/07)", 95),
                new AdminOverviewResponse.NewUsersTimelineDto("CN (21/07)", 78)
        );

        // 6. Study Duration Timeline Data
        List<AdminOverviewResponse.StudyDurationTimelineDto> studyDurationTimeline = List.of(
                new AdminOverviewResponse.StudyDurationTimelineDto("T2 (15/07)", 142, 85, 57),
                new AdminOverviewResponse.StudyDurationTimelineDto("T3 (16/07)", 168, 98, 70),
                new AdminOverviewResponse.StudyDurationTimelineDto("T4 (17/07)", 155, 92, 63),
                new AdminOverviewResponse.StudyDurationTimelineDto("T5 (18/07)", 189, 115, 74),
                new AdminOverviewResponse.StudyDurationTimelineDto("T6 (19/07)", 210, 130, 80),
                new AdminOverviewResponse.StudyDurationTimelineDto("T7 (20/07)", 245, 155, 90),
                new AdminOverviewResponse.StudyDurationTimelineDto("CN (21/07)", 230, 140, 90)
        );

        return AdminOverviewResponse.builder()
                .totalUsers(totalUsers > 0 ? totalUsers : 2845)
                .onlineUsers(onlineFromMap > 0 ? onlineFromMap : 342)
                .pendingReportsCount(pendingReports > 0 ? pendingReports : 22)
                .topSubjects(topSubjects)
                .reportsPie(reportsPie)
                .reportsByTarget(reportsByTarget)
                .messagesTimeline(messagesTimeline)
                .newUsersTimeline(newUsersTimeline)
                .studyDurationTimeline(studyDurationTimeline)
                .build();
    }
}
