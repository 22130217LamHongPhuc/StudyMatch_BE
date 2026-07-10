package com.group_service.mapper;

import java.time.LocalDateTime;

import com.group_service.entity.StudySession;
import com.group_service.entity.StudySessionParticipant;
import com.group_service.entity.enums.StudySessionParticipantRole;
import com.group_service.entity.enums.StudySessionParticipantStatus;

import org.springframework.stereotype.Component;

@Component
public class StudySessionParticipantMapper {

    public StudySessionParticipant mapToStudySessionParticipant(Long memberUserId, boolean isHost,
            String userName,
            StudySession saved) {

        return StudySessionParticipant.builder()
                .sessionId(saved.getId())
                .userId(memberUserId)
                .userName(userName)
                .role(isHost ? StudySessionParticipantRole.HOST
                        : StudySessionParticipantRole.PARTICIPANT)
                .status(isHost ? StudySessionParticipantStatus.ACCEPTED
                        : StudySessionParticipantStatus.PENDING)
                .respondedAt(isHost ? LocalDateTime.now() : null)
                .build();
    }

}
