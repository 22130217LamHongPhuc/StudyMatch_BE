package com.group_service.service.impl;

import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.dto.StudySessionResponse;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.StudySession;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.repository.StudySessionRepository;
import com.group_service.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public StudySessionResponse createSession(Long groupId, CreateStudySessionRequest request) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        if (group.getStatus() == GroupStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group is deleted");
        }

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, request.getCreatedByUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this group");
        }

        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
        }

        StudySession session = StudySession.builder()
                .groupId(groupId)
                .title(request.getTitle().trim())
                .description(normalizeText(request.getDescription()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .studyMode(request.getStudyMode())
                .location(normalizeText(request.getLocation()))
                .createdByUserId(request.getCreatedByUserId())
                .status(GroupStudySessionStatus.SCHEDULED)
                .sessionType(request.getSessionType())
                .build();

        StudySession saved = studySessionRepository.save(session);
        return toResponse(saved);
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private StudySessionResponse toResponse(StudySession s) {
        return new StudySessionResponse(
                s.getId(),
                s.getGroupId(),
                s.getTitle(),
                s.getDescription(),
                s.getStartTime(),
                s.getEndTime(),
                s.getStudyMode(),
                s.getLocation(),
                s.getCreatedByUserId(),
                s.getStatus(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                s.getSubjectName()
        );
    }
}

