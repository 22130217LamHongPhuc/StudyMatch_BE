package com.group_service.mapper;

import java.util.Optional;

import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.dto.StudySessionResponse;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.StudySession;
import com.group_service.entity.StudySessionParticipant;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import com.group_service.entity.enums.StudySessionType;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.repository.StudySessionParticipantRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudySessionMapper {

    private final StudySessionParticipantRepository participantRepository;
    private final StudyGroupRepository studyGroupRepository;

    public StudySession mapToStudySession(Long groupId, CreateStudySessionRequest request) {
        return StudySession.builder()
                .groupId(groupId)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .studyMode(request.getStudyMode())
                .location(request.getLocation())
                .meetingUrl(request.getMeetingUrl())
                .createdByUserId(request.getCreatedByUserId())
                .status(GroupStudySessionStatus.SCHEDULED)
                .sessionType(StudySessionType.GROUP)
                .subjectName(request.getSubjectName())
                .subjectId(request.getSubjectId())
                .reminderSent(false)
                .build();
    }

    public StudySessionResponse mapToStudySessionResponse(StudySession s, Long currentUserId) {

        StudySessionParticipantStatus participantStatus = null;
        if (currentUserId != null) {
            participantStatus = participantRepository.findBySessionIdAndUserId(s.getId(), currentUserId)
                    .map(StudySessionParticipant::getStatus)
                    .orElse(null);
        }

        String groupName = null;
        String groupAvatarUrl = null;
        if (s.getGroupId() != null) {
            Optional<StudyGroup> groupOpt = studyGroupRepository.findById(s.getGroupId());
            groupName = groupOpt.map(StudyGroup::getName).orElse(null);
            groupAvatarUrl = groupOpt.map(StudyGroup::getAvatarUrl).orElse(null);
        }

        String partnerName = null;
        String partnerUserName = null;
        if (s.getSessionType() == StudySessionType.USER_PAIR && currentUserId != null) {
            StudySessionParticipant partner = participantRepository
                    .findFirstBySessionIdAndUserIdNot(s.getId(), currentUserId)
                    .orElse(null);
            if (partner != null) {
                partnerUserName = partner.getUserName();
                partnerName = partner.getUserName();
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
                groupAvatarUrl,
                membersCount,
                s.getSubjectName(),
                s.getCreatedAt(),
                s.getUpdatedAt());
    }
}
