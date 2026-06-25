package com.group_service.service;

import com.group_service.dto.*;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import com.group_service.entity.enums.StudySessionType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

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

    List<StudySessionResponse> getSessionsByGroupId(Long groupId, Long userId);

    StudySessionResponse getSessionById(Long sessionId, Long userId);

    JoinStudySessionResponse joinSession(Long sessionId, Long userId);

    StudySessionResponse respondToSession(Long sessionId, Long userId, StudySessionParticipantStatus status);

    StudySessionResponse updateSessionStatus(Long sessionId, Long userId, GroupStudySessionStatus status);

    void cancelSession(Long sessionId, Long userId);

    StudySessionStatsResponse getSessionStats(Long userId);

    SessionConfirmationStatsResponse getConfirmationStats(Long sessionId, Long userId);

    FeedbackEligibilityResponse getFeedbackEligibility(Long sessionId, Long userId);

    void autoCloseAttendanceLogs(Long sessionId);

    LeaveStudySessionResponse leaveSession(Long sessionId, Long userId, @Valid LeaveStudySessionRequest request);
}
