package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDurationTimelineDto {
    private String date;
    private double totalHours;
    private double onlineSessions;
    private double offlineSessions;
}
