package com.group_service.service.impl;

import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.StudyGroupResponse;
import com.group_service.entity.GroupMember;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.GroupMemberRole;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public StudyGroupResponse createGroup(CreateStudyGroupRequest request) {
        String normalizedName = request.name().trim();
        Long ownerUserId = request.ownerUserId();

        boolean exists = studyGroupRepository.existsByNameIgnoreCaseAndOwnerUserIdAndTermIdAndMainSubjectId(
                normalizedName,
                ownerUserId,
                request.termId(),
                request.mainSubjectId()
        );
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Study group already exists for this owner, term and subject"
            );
        }

        StudyGroup studyGroup = StudyGroup.builder()
                .name(normalizedName)
                .description(normalizeText(request.description()))
                .ownerUserId(ownerUserId)
                .termId(request.termId())
                .studyYearNo(request.studyYearNo().byteValue())
                .semesterNo(request.semesterNo().byteValue())
                .mainSubjectId(request.mainSubjectId())
                .subjectName(normalizeText(request.subjectName()))
                .studyGoal(normalizeText(request.studyGoal()))
                .studyMode(normalizeText(request.studyMode()))
                .maxMembers(request.maxMembers())
                .visibility(normalizeOrDefault(request.visibility(), "PRIVATE"))
                .status("ACTIVE")
                .build();

        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        GroupMember ownerMember = GroupMember.builder()
                .groupId(savedStudyGroup.getId())
                .userId(ownerUserId)
                .role(GroupMemberRole.OWNER)
                .status(GroupMemberStatus.ACTIVE)
                .build();
        groupMemberRepository.save(ownerMember);

        return toResponse(savedStudyGroup);
    }

    private StudyGroupResponse toResponse(StudyGroup studyGroup) {
        return new StudyGroupResponse(
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getDescription(),
                studyGroup.getOwnerUserId(),
                studyGroup.getTermId(),
                studyGroup.getStudyYearNo(),
                studyGroup.getSemesterNo(),
                studyGroup.getMainSubjectId(),
                studyGroup.getSubjectName(),
                studyGroup.getStudyGoal(),
                studyGroup.getStudyMode(),
                studyGroup.getMaxMembers(),
                studyGroup.getVisibility(),
                studyGroup.getStatus(),
                studyGroup.getCreatedAt(),
                studyGroup.getUpdatedAt()
        );
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeOrDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase() : defaultValue;
    }
}

