package com.example.microservice.dto;

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
    private List<SessionInfo> sessions;
    private String groupName;
    private String sessionType;
    private String creatorName;
    private List<Long> userIds;
    private String recurrenceId;
    private String recurrenceType;
    private Integer totalSessions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SessionInfo {
        private Long sessionId;
        private String sessionTitle;
        private String startTime;
        private String meetingUrl;
    }
}
