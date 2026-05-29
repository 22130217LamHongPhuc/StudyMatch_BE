package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionStatsResponse {

    private long todayCount;
    private long thisWeekCount;
    private long pendingCount;
    private long groupSessionCount;
}
