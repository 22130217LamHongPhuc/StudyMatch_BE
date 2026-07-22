package com.group_service.validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.GroupLayout.Group;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.StudySession;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.repository.StudySessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudySessionValidator {

    StudyGroupRepository studyGroupRepository;
    GroupMemberRepository groupMemberRepository;
    StudySessionRepository studySessionRepository;

    public StudyGroup validateGroup(Long groupId, CreateStudySessionRequest request) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        if (group.getStatus() == GroupStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group is deleted");
        }

        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(
                groupId,
                request.getCreatedByUserId(),
                GroupMemberStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an active member of this group");
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());

        return group;
    }

    private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
        }
    }

    public void validateNoOverlap(Long userId, List<StudySession> proposedSessions, Long excludeSessionId) {
        if (proposedSessions == null || proposedSessions.isEmpty()) {
            return;
        }

        LocalDateTime minStart = proposedSessions.stream()
                .map(StudySession::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        List<StudySession> activeSessions = studySessionRepository.findActiveSessionsAfter(userId, minStart);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < proposedSessions.size(); i++) {
            StudySession s1 = proposedSessions.get(i);
            for (int j = i + 1; j < proposedSessions.size(); j++) {
                StudySession s2 = proposedSessions.get(j);
                if (s1.getStartTime().isBefore(s2.getEndTime()) &&
                        s2.getStartTime().isBefore(s1.getEndTime())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            String.format("Các lịch học đăng ký bị trùng nhau: '%s' (%s - %s) và '%s' (%s - %s)",
                                    s1.getTitle(),
                                    s1.getStartTime().format(timeFormatter),
                                    s1.getEndTime().format(timeFormatter),
                                    s2.getTitle(),
                                    s2.getStartTime().format(timeFormatter),
                                    s2.getEndTime().format(timeFormatter)));
                }
            }
        }

        for (StudySession proposed : proposedSessions) {
            for (StudySession existing : activeSessions) {
                if (excludeSessionId != null && existing.getId().equals(excludeSessionId)) {
                    continue;
                }

                if (proposed.getId() != null && existing.getId().equals(proposed.getId())) {
                    continue;
                }

                if (proposed.getStartTime().isBefore(existing.getEndTime()) &&
                        existing.getStartTime().isBefore(proposed.getEndTime())) {
                    
                    String formattedDate = proposed.getStartTime().format(dateFormatter);
                    String existingStartStr = existing.getStartTime().format(timeFormatter);
                    String existingEndStr = existing.getEndTime().format(timeFormatter);
                    String existingDateStr = existing.getStartTime().format(dateFormatter);

                    String existingTimeRange = existingStartStr + " - " + existingEndStr + " ngày " + existingDateStr;
                    
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            String.format("Lịch học vào lúc %s - %s ngày %s bị trùng với lịch '%s' (%s)",
                                    proposed.getStartTime().format(timeFormatter),
                                    proposed.getEndTime().format(timeFormatter),
                                    formattedDate,
                                    existing.getTitle(),
                                    existingTimeRange));
                }
            }
        }
    }

}
