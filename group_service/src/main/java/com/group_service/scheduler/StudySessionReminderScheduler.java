package com.group_service.scheduler;

import com.group_service.clients.ChatClient;
import com.group_service.clients.UserClient;
import com.group_service.dto.ApiResponse;
import com.group_service.dto.BasicUserResponse;
import com.group_service.dto.SessionReminderEmailRequest;
import com.group_service.dto.SessionReminderRequest;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.StudySession;
import com.group_service.entity.StudySessionParticipant;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.repository.StudySessionParticipantRepository;
import com.group_service.repository.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.group_service.service.StudySessionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudySessionReminderScheduler {

    private final StudySessionRepository studySessionRepository;
    private final StudySessionParticipantRepository participantRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final ChatClient chatClient;
    private final UserClient userClient;
    private final StudySessionService studySessionService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    @Scheduled(fixedRate = 60_000)
    public void checkAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinLater = now.plusMinutes(5);

        List<StudySession> upcomingSessions = studySessionRepository.findUpcomingSessions(now, fiveMinLater);

        if (upcomingSessions.isEmpty()) {
            return;
        }

        log.info("tim thay cac nhom can thong bao", upcomingSessions.size());

        for (StudySession session : upcomingSessions) {
            try {
                processSessionReminder(session);
            } catch (Exception e) {
                log.error("fail voi session id={}: {}", session.getId(), e.getMessage());
            }
        }
    }

    private void processSessionReminder(StudySession session) {
        String groupName = getGroupName(session.getGroupId());
        String startTimeFormatted = session.getStartTime().format(FORMATTER);

        List<StudySessionParticipant> participants = participantRepository.findBySessionId(session.getId());

        if (participants.isEmpty()) {
            return;
        }

        List<Long> allUserIds = participants.stream()
                .map(StudySessionParticipant::getUserId)
                .collect(Collectors.toList());

        List<Long> onlineUserIds;
        try {
            onlineUserIds = chatClient.getOnlineUsers(allUserIds);
        } catch (Exception e) {
            log.error("fail khi kiem tra user online ne: {}", e.getMessage());
            onlineUserIds = List.of();
        }

        List<Long> offlineUserIds = new ArrayList<>(allUserIds);
        offlineUserIds.removeAll(onlineUserIds);

        for (Long userId : onlineUserIds) {
            try {
                SessionReminderRequest request = new SessionReminderRequest(
                        session.getId(),
                        userId,
                        session.getTitle(),
                        startTimeFormatted,
                        session.getMeetingUrl(),
                        groupName
                );
                chatClient.sendSessionReminder(request);
                log.info("gui thong bao den user {} cho sesssion {}", userId, session.getId());
            } catch (Exception e) {
                log.error("fail socket khi gui thong bao den user {}: {}", userId, e.getMessage());
            }
        }

        if (!offlineUserIds.isEmpty()) {
            sendEmailReminders(offlineUserIds, session, startTimeFormatted, groupName);
        }

        session.setReminderSent(true);
        studySessionRepository.save(session);
        log.info("ok cho session id={}", session.getId());
    }

    private void sendEmailReminders(List<Long> offlineUserIds, StudySession session, String startTimeFormatted, String groupName) {
        Map<Long, BasicUserResponse> userInfoMap;
        try {
            ApiResponse<List<BasicUserResponse>> response = userClient.getBasicUsers(offlineUserIds);
            List<BasicUserResponse> users = response.getData();
            userInfoMap = users.stream()
                    .collect(Collectors.toMap(BasicUserResponse::getUserId, u -> u));
        } catch (Exception e) {
            log.error("fail khi gui email: {}", e.getMessage());
            return;
        }

        for (Long userId : offlineUserIds) {
            BasicUserResponse userInfo = userInfoMap.get(userId);
            if (userInfo == null || userInfo.getEmail() == null) {
                log.warn("k tim thay thong tiin {}, ", userId);
                continue;
            }

            try {
                SessionReminderEmailRequest emailRequest = new SessionReminderEmailRequest(
                        userId,
                        userInfo.getEmail(),
                        userInfo.getFullName(),
                        session.getTitle(),
                        startTimeFormatted,
                        groupName
                );
                userClient.sendSessionReminderEmail(emailRequest);
                log.info("email den user {} va session {}", userId, session.getId());
            } catch (Exception e) {
                log.error("Fail khi gui mail den user {}: {}", userId, e.getMessage());
            }
        }
    }

    private String getGroupName(Long groupId) {
        if (groupId == null) {
            return "Buổi học cá nhân";
        }
        Optional<StudyGroup> group = studyGroupRepository.findById(groupId);
        return group.map(StudyGroup::getName).orElse("Nhóm học");
    }

    @Scheduled(fixedRate = 60_000)
    public void checkAndCompleteSessions() {
        try {
            studySessionService.autoCompleteEndedSessions();
        } catch (Exception e) {
            log.error("Error running autoCompleteEndedSessions scheduler: {}", e.getMessage());
        }
    }
}
