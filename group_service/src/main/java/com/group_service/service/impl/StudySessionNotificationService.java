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

    public void sendSessionCreatedNotification(StudyGroup group, StudySession saved,
            CreateStudySessionRequest request, List<StudySessionParticipant> participants) {
        try {

            List<Long> recipients = participants.stream()
                    .map(StudySessionParticipant::getUserId)
                    .filter(id -> !id.equals(request.getCreatedByUserId()))
                    .toList();

            String creatorName = participants.stream()
                    .filter(participant -> participant.getUserId().equals(request.getCreatedByUserId()))
                    .map(StudySessionParticipant::getUserName)
                    .findFirst()
                    .orElse(request.getCreatedByUserId().toString());

            StudySessionCreatedRequest notificationReq = StudySessionCreatedRequest.builder()
                    .sessionId(saved.getId())
                    .sessionTitle(saved.getTitle())
                    .startTime(saved.getStartTime().format(FORMATTER))
                    .meetingUrl(saved.getMeetingUrl())
                    .groupName(group.getName())
                    .sessionType(saved.getSessionType().name())
                    .creatorName(creatorName)
                    .userIds(recipients)
                    .build();
            chatClient.sendSessionCreatedNotification(notificationReq);
        } catch (Exception e) {
            log.warn("Failed to send session created notification for sessionId={}: {}",
                    saved.getId(), e.getMessage());
        }

    }
}
