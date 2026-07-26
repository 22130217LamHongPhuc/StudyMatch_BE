package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailedUserStatsResponse {
    private long totalStudyDurationSeconds;
    private double attendanceRate;

    private long joinedCount;
    private long absentCount;
    private long declinedCount;
    private long pendingCount;

    private List<DailyStudyTrend> dailyTrends;
    private List<SubjectStudyStats> subjectStats;
}
