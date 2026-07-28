package com.group_service.service.impl;

import com.group_service.clients.UserClient;
import com.group_service.dto.AdminSessionStatsResponse;
import com.group_service.dto.AdminStudySessionResponse;
import com.group_service.dto.ApiResponse;
import com.group_service.dto.BasicUserResponse;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.StudySession;
import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionType;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.repository.StudySessionRepository;
import com.group_service.service.AdminStudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStudySessionServiceImpl implements AdminStudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserClient userClient;

    @Override
    @Transactional(readOnly = true)
    public AdminSessionStatsResponse getSessionStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plus(7, ChronoUnit.DAYS);

        long totalSessions = studySessionRepository.count();

        long upcomingSessions = countSessionsByStatus(now, sevenDaysLater, null);

        long ongoingSessions = studySessionRepository.findAll().stream()
                .filter(s -> s.getStartTime().isBefore(now) && s.getEndTime().isAfter(now))
                .count();

        long completedCancelledSessions = studySessionRepository.findAll().stream()
                .filter(s -> s.getStatus() == GroupStudySessionStatus.COMPLETED || 
                            s.getStatus() == GroupStudySessionStatus.CANCELLED)
                .count();

        double completionPercentage = totalSessions > 0 
                ? (double) completedCancelledSessions / totalSessions * 100 
                : 0.0;

        return AdminSessionStatsResponse.builder()
                .totalSessions(totalSessions)
                .upcomingSessions(upcomingSessions)
                .ongoingSessions(ongoingSessions)
                .completedCancelledSessions(completedCancelledSessions)
                .completionPercentage(Math.round(completionPercentage * 10.0) / 10.0)
                .build();
    }

    private long countSessionsByStatus(LocalDateTime startTime, LocalDateTime endTime, GroupStudySessionStatus status) {
        return studySessionRepository.findAll().stream()
                .filter(s -> s.getStartTime().isAfter(startTime) && 
                            s.getStartTime().isBefore(endTime) &&
                            s.getStatus() == GroupStudySessionStatus.SCHEDULED)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminStudySessionResponse> getSessionsForAdmin(
            String keyword,
            GroupStudySessionStatus status,
            GroupStudySessionMode studyMode,
            StudySessionType sessionType,
            LocalDateTime startFrom,
            LocalDateTime startTo,
            int page,
            int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        String keywordParam = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim().toLowerCase() + "%" : null;

        Page<StudySession> sessions = studySessionRepository.findAdminSessionsWithFilters(
                keywordParam, status, studyMode, sessionType, startFrom, startTo, pageable
        );

        if (sessions.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> groupIds = sessions.stream()
                .map(StudySession::getGroupId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, StudyGroup> groupMap = new HashMap<>();
        if (!groupIds.isEmpty()) {
            groupMap = studyGroupRepository.findAllById(groupIds).stream()
                    .collect(Collectors.toMap(StudyGroup::getId, g -> g));
        }

        List<Long> creatorIds = sessions.stream()
                .map(StudySession::getCreatedByUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> userNamesMap = fetchUserNames(creatorIds);

        List<Long> sessionIds = sessions.stream()
                .map(StudySession::getId)
                .toList();

        List<Object[]> rawCounts = studySessionRepository.countParticipantsBySessionIds(sessionIds);
        Map<Long, Long> participantCountsMap = rawCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        Map<Long, StudyGroup> finalGroupMap = groupMap;
        Map<Long, String> finalUserNamesMap = userNamesMap;
        return sessions.map(s -> {
            StudyGroup group = s.getGroupId() != null ? finalGroupMap.get(s.getGroupId()) : null;
            String groupName = group != null ? group.getName() : null;
            Integer maxMembers = s.getSessionType() == StudySessionType.USER_PAIR ? 2 : (group != null ? group.getMaxMembers() : null);
            Long membersCount = participantCountsMap.getOrDefault(s.getId(), 0L);
            String creatorName = finalUserNamesMap.getOrDefault(s.getCreatedByUserId(), "User #" + s.getCreatedByUserId());

            return AdminStudySessionResponse.builder()
                    .id(s.getId())
                    .title(s.getTitle())
                    .subjectName(s.getSubjectName())
                    .groupName(groupName)
                    .groupAvatarUrl(group != null ? group.getAvatarUrl() : null)
                    .sessionType(s.getSessionType())
                    .creatorName(creatorName)
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .studyMode(s.getStudyMode())
                    .membersCount(membersCount)
                    .maxMembers(maxMembers)
                    .status(s.getStatus())
                    .build();
        });
    }

    private Map<Long, String> fetchUserNames(List<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        try {
            ApiResponse<List<BasicUserResponse>> response = userClient.getBasicUsers(userIds);
            if (response != null && response.getData() != null) {
                for (BasicUserResponse user : response.getData()) {
                    if (user != null && user.getUserId() != null) {
                        result.put(user.getUserId(), user.getFullName());
                    }
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    @Transactional
    public void cancelSessionForAdmin(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study session not found"));

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
    @Transactional
    public void deleteSessionForAdmin(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study session not found"));

        studySessionRepository.delete(session);
    }
}

