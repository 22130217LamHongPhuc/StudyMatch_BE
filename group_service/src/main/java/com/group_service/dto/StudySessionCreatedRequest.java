package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySessionCreatedRequest {
    private Long sessionId;
    private String sessionTitle;
    private String startTime;
    private String meetingUrl;
    private String groupName;
    private String sessionType;
    private String creatorName;
    private List<Long> userIds;
}
