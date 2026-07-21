package com.example.microservice.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOverviewResponse {
    private long totalUsers;
    private long onlineUsers;
    private long pendingReportsCount;

    private List<SubjectGroupStatDto> topSubjects;
    private List<ReportStatusStatDto> reportsPie;
    private List<ReportTargetStatDto> reportsByTarget;
    private List<MessagesTimelineDto> messagesTimeline;
    private List<NewUsersTimelineDto> newUsersTimeline;
    private List<StudyDurationTimelineDto> studyDurationTimeline;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectGroupStatDto {
        private String subjectName;
        private int publicCount;
        private int privateCount;
        private int totalGroups;
        private int totalMembers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportStatusStatDto {
        private String name;
        private long value;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportTargetStatDto {
        private String name;
        private long pending;
        private long reviewing;
        private long resolved;
        private long rejected;
        private long total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessagesTimelineDto {
        private String date;
        private long groupMessages;
        private long privateMessages;
        private long total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewUsersTimelineDto {
        private String date;
        private long newUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyDurationTimelineDto {
        private String date;
        private double totalHours;
        private double onlineSessions;
        private double offlineSessions;
    }
}
