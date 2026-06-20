package com.group_service.service.impl;

import com.group_service.clients.UserClient;
import com.group_service.dto.*;
import com.group_service.entity.*;
import com.group_service.entity.enums.*;
import com.group_service.enums.StatusCode;
import com.group_service.exception.AppException;
import com.group_service.repository.*;
import com.group_service.service.StudySessionService;
import com.group_service.service.ZegoCloudTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final StudySessionParticipantRepository participantRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserClient userClient;
    private final ZegoCloudTokenService zegoCloudTokenService;
    private final StudySessionAttendanceLogRepository attendanceLogRepository;

    @Override
    @Transactional
    public StudySessionResponse createSession(Long groupId, CreateStudySessionRequest request) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        if (group.getStatus() == GroupStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group is deleted");
        }

        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(
                groupId,
                request.getCreatedByUserId(),
                GroupMemberStatus.ACTIVE
        )) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an active member of this group");
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());

        StudySession session = StudySession.builder()
                .groupId(groupId)
                .title(request.getTitle().trim())
                .description(normalizeText(request.getDescription()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .studyMode(request.getStudyMode())
                .location(normalizeText(request.getLocation()))
                .meetingUrl(normalizeText(request.getMeetingUrl()))
                .createdByUserId(request.getCreatedByUserId())
                .status(GroupStudySessionStatus.SCHEDULED)
                .sessionType(StudySessionType.GROUP)
                .subjectName(request.getSubjectName())
                .subjectId(request.getSubjectId())
                .reminderSent(false)
                .build();

        StudySession saved = studySessionRepository.save(session);

        List<GroupMember> activeMembers = groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE);
        if (activeMembers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group has no active members");
        }

        List<Long> userIds = activeMembers.stream()
                .map(GroupMember::getUserId)
                .distinct()
                .toList();

        Map<Long, String> userNames = fetchUserNames(userIds);
        List<StudySessionParticipant> participants = activeMembers.stream()
                .map(member -> {
                    Long memberUserId = member.getUserId();
                    boolean isHost = memberUserId.equals(request.getCreatedByUserId());
                    String userName = userNames.getOrDefault(memberUserId, fallbackUserName(memberUserId));

                    return StudySessionParticipant.builder()
                            .sessionId(saved.getId())
                            .userId(memberUserId)
                            .userName(userName)
                            .role(isHost ? StudySessionParticipantRole.HOST : StudySessionParticipantRole.PARTICIPANT)
                            .status(isHost ? StudySessionParticipantStatus.ACCEPTED : StudySessionParticipantStatus.PENDING)
                            .respondedAt(isHost ? LocalDateTime.now() : null)
                            .build();
                })
                .toList();

        participantRepository.saveAll(participants);

        return toResponse(saved, request.getCreatedByUserId());
    }

    @Override
    @Transactional
    public StudySessionResponse createPairSession(CreateStudySessionRequest request) {
        if (request.getPartnerUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "partnerUserId is required for pair session");
        }

        if (!StringUtils.hasText(request.getPartnerUserName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "partnerUserName is required for pair session");
        }

        if (request.getCreatedByUserId().equals(request.getPartnerUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create pair session with yourself");
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());

        StudySession session = StudySession.builder()
                .groupId(null)
                .title(request.getTitle().trim())
                .description(normalizeText(request.getDescription()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .studyMode(request.getStudyMode())
                .location(normalizeText(request.getLocation()))
                .meetingUrl(normalizeText(request.getMeetingUrl()))
                .createdByUserId(request.getCreatedByUserId())
                .status(GroupStudySessionStatus.SCHEDULED)
                .sessionType(StudySessionType.USER_PAIR)
                .subjectName(request.getSubjectName())
                .reminderSent(false)
                .subjectId(request.getSubjectId())
                .build();

        StudySession saved = studySessionRepository.save(session);

        Map<Long, String> userNames = fetchUserNames(List.of(request.getCreatedByUserId(), request.getPartnerUserId()));
        String hostUserName = userNames.getOrDefault(
                request.getCreatedByUserId(),
                fallbackUserName(request.getCreatedByUserId())
        );
        String partnerUserName = userNames.get(request.getPartnerUserId());
        if (!StringUtils.hasText(partnerUserName)) {
            partnerUserName = normalizeText(request.getPartnerUserName());
        }
        if (!StringUtils.hasText(partnerUserName)) {
            partnerUserName = fallbackUserName(request.getPartnerUserId());
        }

        StudySessionParticipant host = StudySessionParticipant.builder()
                .sessionId(saved.getId())
                .userId(request.getCreatedByUserId())
                .userName(hostUserName)
                .role(StudySessionParticipantRole.HOST)
                .status(StudySessionParticipantStatus.ACCEPTED)
                .respondedAt(LocalDateTime.now())
                .build();

        StudySessionParticipant partner = StudySessionParticipant.builder()
                .sessionId(saved.getId())
                .userId(request.getPartnerUserId())
                .userName(partnerUserName)
                .role(StudySessionParticipantRole.PARTICIPANT)
                .status(StudySessionParticipantStatus.PENDING)
                .build();

        participantRepository.save(host);
        participantRepository.save(partner);

        return toResponse(saved, request.getCreatedByUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudySessionResponse> getSessionsByUserId(
            Long userId,
            StudySessionType sessionType,
            StudySessionParticipantStatus participantStatus,
            GroupStudySessionStatus sessionStatus,
            LocalDateTime startFrom,
            LocalDateTime startTo,
            Pageable pageable
    ) {
        Page<StudySession> sessions = studySessionRepository.findSessionsByUserIdWithFilters(
                userId, sessionType, participantStatus, sessionStatus, startFrom, startTo, pageable
        );

        return sessions.map(session -> toResponse(session, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public StudySessionResponse getSessionById(Long sessionId, Long userId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study session not found"));

        return toResponse(session, userId);
    }

    @Transactional
    @Override
    public JoinStudySessionResponse joinSession(Long sessionId, Long userId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException("study session not found"));

        StudySessionParticipant participant = participantRepository
                .findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new AppException("you are not a participant of this session"));

        if (participant.getStatus() != StudySessionParticipantStatus.ACCEPTED
                && participant.getStatus() != StudySessionParticipantStatus.JOINED) {
            throw new AppException("you can't joined into session");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(session.getStartTime().minusMinutes(5))) {
            throw new AppException("session has not started yet. You can join from 5 minutes before start time");
        }

        if (now.isAfter(session.getEndTime())) {
            throw new AppException("session has already ended. You can join until 5 minutes after end time");
        }

        Optional<StudySessionAttendanceLog> openingLog =
                attendanceLogRepository.findFirstBySessionIdAndUserIdAndLeftAtIsNullOrderByJoinedAtDesc(
                        sessionId,
                        userId
                );

        StudySessionAttendanceLog log;

        if (openingLog.isPresent()) {
            log = openingLog.get();
        } else {
            log = StudySessionAttendanceLog.builder()
                    .sessionId(sessionId)
                    .participantId(participant.getId())
                    .userId(userId)
                    .joinedAt(now)
                    .build();

            log = attendanceLogRepository.save(log);

            if (participant.getFirstJoinedAt() == null) {
                participant.setFirstJoinedAt(now);
            }

            participant.setJoinCount(
                    participant.getJoinCount() == null ? 1 : participant.getJoinCount() + 1
            );

            participant.setStatus(StudySessionParticipantStatus.JOINED);
            participantRepository.save(participant);
        }
        String roomId = ensureRoomId(session);


        String token = zegoCloudTokenService.generateToken(userId, roomId);


//        return JoinStudySessionResponse.builder()
//                .sessionId(session.getId())
//                .userId(userId)
//                .attendanceLogId(log.getId())
//                .meetingUrl(session.getMeetingUrl())
//                .roomId(session.getRoomId())
//                .joinedAt(log.getJoinedAt())
//                .joinCount(participant.getJoinCount())
//                .totalDurationSeconds(participant.getTotalDurationSeconds())
//                .attendanceStatus(participant.getAttendanceStatus())
//                .build();

        return new JoinStudySessionResponse(
                session.getId(),
                roomId,
                token,
                now
        );
    }

    @Override
    @Transactional
    public StudySessionResponse respondToSession(Long sessionId, Long userId, StudySessionParticipantStatus status) {
        if (status != StudySessionParticipantStatus.ACCEPTED && status != StudySessionParticipantStatus.DECLINED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be ACCEPTED or DECLINED");
        }

        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study session not found"));

        if (session.getStatus() == GroupStudySessionStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot respond to a cancelled session");
        }

        StudySessionParticipant participant = participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this session"));

        if (participant.getStatus() != StudySessionParticipantStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already responded to this invitation");
        }

        participant.setStatus(status);
        participant.setRespondedAt(LocalDateTime.now());
        participantRepository.save(participant);

        return toResponse(session, userId);
    }


    @Transactional
    public void autoCloseAttendanceLogs(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(StatusCode.SESSION_NOT_FOUND));

        List<StudySessionAttendanceLog> openingLogs =
                attendanceLogRepository.findBySessionIdAndLeftAtIsNull(sessionId);

        for (StudySessionAttendanceLog log : openingLogs) {
            LocalDateTime autoLeftAt = calculateAutoLeftAt(log.getJoinedAt(), session.getEndTime());

            long durationSeconds = Duration.between(log.getJoinedAt(), autoLeftAt).getSeconds();

            if (durationSeconds < 0) {
                durationSeconds = 0;
            }

            log.setLeftAt(autoLeftAt);
            log.setDurationSeconds(durationSeconds);
            log.setLeaveReason(StudySessionLeaveReason.AUTO_CLOSED);
            attendanceLogRepository.save(log);

            participantRepository.findBySessionIdAndUserId(sessionId, log.getUserId())
                    .ifPresent(participant -> {
                        Long totalDuration = attendanceLogRepository.sumDurationSecondsBySessionIdAndUserId(
                                sessionId,
                                log.getUserId()
                        );

                        participant.setLastLeftAt(autoLeftAt);
                        participant.setTotalDurationSeconds(totalDuration);
                        participant.setAttendanceStatus(calculateAttendanceStatus(session, totalDuration));
                        participantRepository.save(participant);
                    });
        }
    }
    @Override
    @Transactional
    public StudySessionResponse updateSessionStatus(Long sessionId, Long userId, GroupStudySessionStatus newStatus) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study session not found"));

        if (!session.getCreatedByUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the session creator can update the status");
        }

        validateStatusTransition(session.getStatus(), newStatus);

        session.setStatus(newStatus);
        StudySession saved = studySessionRepository.save(session);

        return toResponse(saved, userId);
    }

    @Override
    @Transactional
    public void cancelSession(Long sessionId, Long userId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study session not found"));

        if (!session.getCreatedByUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the session creator can cancel the session");
        }

        if (session.getStatus() == GroupStudySessionStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a completed session");
        }

        if (session.getStatus() == GroupStudySessionStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session is already cancelled");
        }

        session.setStatus(GroupStudySessionStatus.CANCELLED);
        studySessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionConfirmationStatsResponse getConfirmationStats(Long sessionId, Long userId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study session not found"));

        boolean hasAccess = switch (session.getSessionType()) {
            case GROUP -> session.getCreatedByUserId().equals(userId);
            case USER_PAIR -> participantRepository.existsBySessionIdAndUserId(sessionId, userId);
        };

        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view confirmation stats for this session");
        }

        List<StudySessionParticipant> participantsDb = participantRepository.findBySessionId(sessionId);
        List<SessionParticipantConfirmationResponse> participants = participantsDb.stream()
                .map(participant -> new SessionParticipantConfirmationResponse(
                        participant.getUserId(),
                        resolveParticipantUserName(participant),
                        participant.getRole(),
                        participant.getStatus(),
                        participant.getRespondedAt()
                ))
                .toList();

        List<SessionParticipantConfirmationResponse> anotherParticipants = participantsDb.stream()
                .filter(participant -> !participant.getUserId().equals(userId))
                .map(participant -> new SessionParticipantConfirmationResponse(
                        participant.getUserId(),
                        resolveParticipantUserName(participant),
                        participant.getRole(),
                        participant.getStatus(),
                        participant.getRespondedAt()
                ))
                .toList();

        long acceptedCount = participants.stream()
                .filter(participant -> participant.status() == StudySessionParticipantStatus.ACCEPTED || participant.status() ==  StudySessionParticipantStatus.JOINED)
                .count();
        long pendingCount = participants.stream()
                .filter(participant -> participant.status() == StudySessionParticipantStatus.PENDING)
                .count();
        long declinedCount = participants.stream()
                .filter(participant -> participant.status() == StudySessionParticipantStatus.DECLINED)
                .count();

        return new SessionConfirmationStatsResponse(
                session.getId(),
                session.getSessionType(),
                userId,
                participants.size() ,
                acceptedCount,
                pendingCount,
                declinedCount,
                anotherParticipants
        );
    }

    @Transactional
    @Override
    public FeedbackEligibilityResponse getFeedbackEligibility(Long sessionId, Long userId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(StatusCode.SESSION_NOT_FOUND));

        StudySessionParticipant participant = participantRepository
                .findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new AppException(StatusCode.PARTICIPANT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        boolean sessionEnded = now.isAfter(session.getEndTime());

        if (sessionEnded) {
            autoCloseAttendanceLogs(sessionId);
            participant = participantRepository.findBySessionIdAndUserId(sessionId, userId)
                    .orElseThrow(() -> new AppException(StatusCode.PARTICIPANT_NOT_FOUND));
        }

        Long totalDuration = participant.getTotalDurationSeconds() == null
                ? 0L
                : participant.getTotalDurationSeconds();

        Long minRequiredDuration = calculateMinRequiredDurationSeconds(session);

        StudySessionAttendanceStatus attendanceStatus = calculateAttendanceStatus(session, totalDuration);

        boolean canSubmitFeedback = false;
        boolean eligibleForModel = false;
        StudyFeedbackType feedbackType;
        String message;

        if (!sessionEnded) {
            feedbackType = null;
            message = "Buổi học chưa kết thúc";
        } else if (attendanceStatus == StudySessionAttendanceStatus.COMPLETED) {
            canSubmitFeedback = true;
            eligibleForModel = true;
            feedbackType = StudyFeedbackType.SESSION_FEEDBACK;
            message = "Bạn có thể đánh giá buổi học";
        }  else if (attendanceStatus == StudySessionAttendanceStatus.JOINED_SHORT) {
            feedbackType = StudyFeedbackType.EARLY_LEAVE_REASON;
            message = "Bạn đã rời buổi học khá sớm, vui lòng cho biết lý do";
        } else {
            feedbackType = StudyFeedbackType.PARTIAL_FEEDBACK;
            message = "Bạn có thể gửi phản hồi ngắn, nhưng chưa đủ điều kiện đánh giá chính thức";
        }

        Long targetUserId = null;

        if (session.getSessionType() == StudySessionType.USER_PAIR) {
            targetUserId = findTargetUserIdForPairSession(sessionId, userId);
        }

        return FeedbackEligibilityResponse.builder()
                .sessionId(sessionId)
                .userId(userId)
                .sessionType(session.getSessionType())
                .targetUserId(targetUserId)
                .groupId(session.getGroupId())
                .sessionEnded(sessionEnded)
                .canSubmitFeedback(canSubmitFeedback)
                .feedbackType(feedbackType)
                .totalDurationSeconds(totalDuration)
                .minRequiredDurationSeconds(minRequiredDuration)
                .attendanceStatus(attendanceStatus)
                .eligibleForModel(eligibleForModel)
                .message(message)
                .build();
    }

    private Long findTargetUserIdForPairSession(Long sessionId, Long reviewerUserId) {
        return participantRepository.findBySessionId(sessionId)
                .stream()
                .map(StudySessionParticipant::getUserId)
                .filter(id -> !id.equals(reviewerUserId))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public StudySessionStatsResponse getSessionStats(Long userId) {

        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(7);
        LocalDateTime weekStartDt = weekStart.atStartOfDay();
        LocalDateTime weekEndDt = weekEnd.atStartOfDay();

        long todayCount = studySessionRepository.countTodaySessions(userId, dayStart, dayEnd);
        long thisWeekCount = studySessionRepository.countWeekSessions(userId, weekStartDt, weekEndDt);
        long pendingCount = studySessionRepository.countPendingSessions(userId);
        long groupSessionCount = studySessionRepository.countGroupSessions(userId);

        return StudySessionStatsResponse.builder()
                .todayCount(todayCount)
                .thisWeekCount(thisWeekCount)
                .pendingCount(pendingCount)
                .groupSessionCount(groupSessionCount)
                .build();
    }

    private StudySessionResponse toResponse(StudySession s, Long currentUserId) {
        StudySessionParticipantStatus participantStatus = null;
        if (currentUserId != null) {
            participantStatus = participantRepository.findBySessionIdAndUserId(s.getId(), currentUserId)
                    .map(StudySessionParticipant::getStatus)
                    .orElse(null);
        }

        String groupName = null;
        if (s.getGroupId() != null) {
            groupName = studyGroupRepository.findById(s.getGroupId())
                    .map(StudyGroup::getName)
                    .orElse(null);
        }

        String partnerName = null;
        String partnerUserName = null;
        if (s.getSessionType() == StudySessionType.USER_PAIR && currentUserId != null) {
            StudySessionParticipant partner = participantRepository
                    .findFirstBySessionIdAndUserIdNot(s.getId(), currentUserId)
                    .orElse(null);
            if (partner != null) {
                partnerUserName = normalizeText(partner.getUserName());
                partnerName = fetchUserFullName(partner.getUserId());
            }
        }

        long membersCount = participantRepository.countBySessionId(s.getId());

        return new StudySessionResponse(
                s.getId(),
                s.getSessionType(),
                s.getGroupId(),
                s.getTitle(),
                s.getDescription(),
                s.getStartTime(),
                s.getEndTime(),
                s.getStudyMode(),
                s.getLocation(),
                s.getMeetingUrl(),
                s.getRoomId(),
                s.getCreatedByUserId(),
                s.getStatus(),
                participantStatus,
                partnerName,
                partnerUserName,
                groupName,
                membersCount,
                s.getSubjectName(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

    private String fetchUserFullName(Long userId) {
        try {
            Map<String, Object> userData = userClient.getUserById(userId);
            if (userData != null && userData.containsKey("fullName")) {
                return (String) userData.get("fullName");
            }
            if (userData != null && userData.containsKey("full_name")) {
                return (String) userData.get("full_name");
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user name for userId={}: {}", userId, e.getMessage());
        }
        return "User #" + userId;
    }

    private Map<Long, String> fetchUserNames(List<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        try {
            ApiResponse<List<BasicUserResponse>> response = userClient.getBasicUsers(userIds);
            if (response == null || response.getData() == null) {
                return result;
            }
            for (BasicUserResponse user : response.getData()) {
                if (user == null || user.getUserId() == null) {
                    continue;
                }
                String userName = normalizeText(user.getUserName());
                if (!StringUtils.hasText(userName)) {
                    userName = normalizeText(user.getFullName());
                }
                if (StringUtils.hasText(userName)) {
                    result.put(user.getUserId(), userName);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch basic users for ids={}: {}", userIds, e.getMessage());
        }
        return result;
    }

    private String fallbackUserName(Long userId) {
        return "User #" + userId;
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
        }
    }

    private void validateJoinWindow(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime now) {
        LocalDateTime joinStart = startTime.minusMinutes(5);
        LocalDateTime joinEnd = endTime.plusMinutes(5);

        if (now.isBefore(joinStart) || now.isAfter(joinEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session can only be joined from 5 minutes before start until 5 minutes after end");
        }
    }

    private String ensureRoomId(StudySession session) {
        if (StringUtils.hasText(session.getRoomId())) {
            return session.getRoomId();
        }

        String roomId = generateRoomId(session.getId());
        session.setRoomId(roomId);
        return roomId;
    }

    private String generateRoomId(Long sessionId) {
        return "study-session-" + sessionId;
    }

    private void validateStatusTransition(GroupStudySessionStatus current, GroupStudySessionStatus target) {
        boolean valid = switch (current) {
            case SCHEDULED -> target == GroupStudySessionStatus.ONGOING || target == GroupStudySessionStatus.CANCELLED;
            case ONGOING -> target == GroupStudySessionStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status transition: " + current + " -> " + target);
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String resolveParticipantUserName(StudySessionParticipant participant) {
        if (StringUtils.hasText(participant.getUserName())) {
            return participant.getUserName();
        }
        return fallbackUserName(participant.getUserId());
    }

    @Transactional
    @Override
    public LeaveStudySessionResponse leaveSession(
            Long sessionId,
            Long userId,
            LeaveStudySessionRequest request
    ) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Study session not found"));

        StudySessionParticipant participant = participantRepository
                .findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("You are not a participant of this session"));

        StudySessionAttendanceLog log = attendanceLogRepository
                .findFirstBySessionIdAndUserIdAndLeftAtIsNullOrderByJoinedAtDesc(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("No active attendance log found for this session"));
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime leftAt = now.isAfter(session.getEndTime())
                ? session.getEndTime()
                : now;

        long durationSeconds = Duration.between(log.getJoinedAt(), leftAt).getSeconds();

        if (durationSeconds < 0) {
            durationSeconds = 0;
        }

        StudySessionLeaveReason reason = StudySessionLeaveReason.USER_LEFT;

        if (request != null && request.getLeaveReason() != null) {
            reason = request.getLeaveReason();
        }

        log.setLeftAt(leftAt);
        log.setDurationSeconds(durationSeconds);
        log.setLeaveReason(reason);
        attendanceLogRepository.save(log);

        Long totalDuration = attendanceLogRepository.sumDurationSecondsBySessionIdAndUserId(sessionId, userId);

        participant.setLastLeftAt(leftAt);
        participant.setTotalDurationSeconds(totalDuration);
        participant.setAttendanceStatus(calculateAttendanceStatus(session, totalDuration));
        participantRepository.save(participant);

        return LeaveStudySessionResponse.builder()
                .sessionId(sessionId)
                .userId(userId)
                .attendanceLogId(log.getId())
                .joinedAt(log.getJoinedAt())
                .leftAt(leftAt)
                .durationSeconds(durationSeconds)
                .totalDurationSeconds(totalDuration)
                .joinCount(participant.getJoinCount())
                .attendanceStatus(participant.getAttendanceStatus())
                .build();
    }

    private LocalDateTime calculateAutoLeftAt(LocalDateTime joinedAt, LocalDateTime sessionEndTime) {
        LocalDateTime maxAutoLeftAt = joinedAt.plusMinutes(30);

        if (maxAutoLeftAt.isBefore(sessionEndTime)) {
            return maxAutoLeftAt;
        }

        return sessionEndTime;
    }

    private StudySessionAttendanceStatus calculateAttendanceStatus(
            StudySession session,
            Long totalDurationSeconds
    ) {
        long total = totalDurationSeconds == null ? 0L : totalDurationSeconds;

        if (total <= 0) {
            return StudySessionAttendanceStatus.NOT_JOINED;
        }

        if (total < 1 * 60) {
            return StudySessionAttendanceStatus.JOINED_SHORT;
        }

        long minRequired = calculateMinRequiredDurationSeconds(session);

        if (total < minRequired) {
            return StudySessionAttendanceStatus.JOINED_PARTIAL;
        }

        return StudySessionAttendanceStatus.COMPLETED;
    }

    private long calculateMinRequiredDurationSeconds(StudySession session) {
        long sessionDurationSeconds = Duration.between(
                session.getStartTime(),
                session.getEndTime()
        ).getSeconds();

        long tenMinutes = 1 * 60L;
        long thirtyPercent = Math.round(sessionDurationSeconds * 0.3);

        return Math.max(tenMinutes, thirtyPercent);
    }


}
