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
public class AdminSessionStatsResponse {
    private Long totalSessions;
    private Long upcomingSessions;
    private Long ongoingSessions;
    private Long completedCancelledSessions;
    private Double completionPercentage;
}

