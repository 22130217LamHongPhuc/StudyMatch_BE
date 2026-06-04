package com.group_service.service;

import com.group_service.dto.AdminSessionStatsResponse;
import com.group_service.dto.AdminStudySessionResponse;
import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface AdminStudySessionService {
    AdminSessionStatsResponse getSessionStats();

    Page<AdminStudySessionResponse> getSessionsForAdmin(
            String keyword,
            GroupStudySessionStatus status,
            GroupStudySessionMode studyMode,
            StudySessionType sessionType,
            LocalDateTime startFrom,
            LocalDateTime startTo,
            int page,
            int limit
    );
}

