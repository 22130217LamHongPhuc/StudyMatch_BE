package com.group_service.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.group_service.clients.ChatClient;
import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.dto.StudySessionCreatedRequest;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.StudySession;
import com.group_service.entity.StudySessionParticipant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudySessionNotificationService {

    private final ChatClient chatClient;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public void sendSessionCreatedNotification(StudyGroup group, List<StudySession> savedSessions,
            CreateStudySessionRequest request, List<StudySessionParticipant> participants, Integer totalSessions) {
        try {
            if (savedSessions == null || savedSessions.isEmpty()) {
                return;
            }
            StudySession firstSession = savedSessions.get(0);

            List<Long> recipients = participants.stream()
                    .map(StudySessionParticipant::getUserId)
                    .filter(id -> !id.equals(request.getCreatedByUserId()))
                    .toList();

            String creatorName = participants.stream()
                    .filter(participant -> participant.getUserId().equals(request.getCreatedByUserId()))
                    .map(StudySessionParticipant::getUserName)
                    .findFirst()
                    .orElse(request.getCreatedByUserId().toString());

            String groupName = group.getName();
            if (firstSession.getRecurrenceId() != null) {
                groupName += " (Lịch lặp)";
            }

            List<StudySessionCreatedRequest.SessionInfo> sessionsInfo = savedSessions.stream()
                    .map(s -> StudySessionCreatedRequest.SessionInfo.builder()
                            .sessionId(s.getId())
                            .sessionTitle(s.getTitle())
                            .startTime(s.getStartTime().format(FORMATTER))
                            .meetingUrl(s.getMeetingUrl())
                            .build())
                    .toList();

            StudySessionCreatedRequest notificationReq = StudySessionCreatedRequest.builder()
                    .sessions(sessionsInfo)
                    .groupName(groupName)
                    .sessionType(firstSession.getSessionType().name())
                    .creatorName(creatorName)
                    .userIds(recipients)
                    .recurrenceId(firstSession.getRecurrenceId())
                    .recurrenceType(request.getRecurrenceType())
                    .totalSessions(totalSessions)
                    .build();
            chatClient.sendSessionCreatedNotification(notificationReq);
        } catch (Exception e) {
            log.warn("Failed to send session created notification: {}", e.getMessage());
        }

    }
}
