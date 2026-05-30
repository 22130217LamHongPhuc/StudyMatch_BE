package com.group_service.service.impl;

import com.group_service.clients.UserClient;
import com.group_service.dto.ApiResponse;
import com.group_service.dto.BasicUserResponse;
import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.dto.SessionConfirmationStatsResponse;
import com.group_service.dto.SessionParticipantConfirmationResponse;
import com.group_service.dto.StudySessionResponse;
import com.group_service.dto.StudySessionStatsResponse;
import com.group_service.entity.GroupMember;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.StudySession;
import com.group_service.entity.StudySessionParticipant;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionParticipantRole;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import com.group_service.entity.enums.StudySessionType;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.repository.StudySessionParticipantRepository;
import com.group_service.repository.StudySessionRepository;
import com.group_service.service.StudySessionService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final StudySessionParticipantRepository participantRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserClient userClient;

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

        List<StudySessionParticipant> participants = participantRepository.findBySessionId(sessionId);
        List<SessionParticipantConfirmationResponse> otherParticipants = participants.stream()
                .filter(participant -> !participant.getUserId().equals(userId))
                .map(participant -> new SessionParticipantConfirmationResponse(
                        participant.getUserId(),
                        resolveParticipantUserName(participant),
                        participant.getRole(),
                        participant.getStatus(),
                        participant.getRespondedAt()
                ))
                .toList();

        long acceptedCount = otherParticipants.stream()
                .filter(participant -> participant.status() == StudySessionParticipantStatus.ACCEPTED)
                .count();
        acceptedCount++;
        long pendingCount = otherParticipants.stream()
                .filter(participant -> participant.status() == StudySessionParticipantStatus.PENDING)
                .count();
        long declinedCount = otherParticipants.stream()
                .filter(participant -> participant.status() == StudySessionParticipantStatus.DECLINED)
                .count();

        return new SessionConfirmationStatsResponse(
                session.getId(),
                session.getSessionType(),
                userId,
                otherParticipants.size() + 1,
                acceptedCount,
                pendingCount,
                declinedCount,
                otherParticipants
        );
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
}
