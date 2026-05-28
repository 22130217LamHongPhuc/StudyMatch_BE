package com.group_service.service;

import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.dto.StudySessionResponse;
import com.group_service.dto.StudySessionStatsResponse;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import com.group_service.entity.enums.StudySessionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface StudySessionService {

    StudySessionResponse createSession(Long groupId, CreateStudySessionRequest request);

    StudySessionResponse createPairSession(CreateStudySessionRequest request);

    Page<StudySessionResponse> getSessionsByUserId(
            Long userId,
            StudySessionType sessionType,
            StudySessionParticipantStatus participantStatus,
            GroupStudySessionStatus sessionStatus,
            LocalDateTime startFrom,
            LocalDateTime startTo,
            Pageable pageable
    );

    StudySessionResponse getSessionById(Long sessionId, Long userId);

    StudySessionResponse respondToSession(Long sessionId, Long userId, StudySessionParticipantStatus status);

    StudySessionResponse updateSessionStatus(Long sessionId, Long userId, GroupStudySessionStatus status);

    void cancelSession(Long sessionId, Long userId);

    StudySessionStatsResponse getSessionStats(Long userId);
}
