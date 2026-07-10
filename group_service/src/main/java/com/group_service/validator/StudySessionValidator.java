package com.group_service.validator;

import java.time.LocalDateTime;

import javax.swing.GroupLayout.Group;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudyGroupRepository;

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

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
        }
    }

}
