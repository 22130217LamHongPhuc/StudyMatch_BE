package com.group_service.service.impl;

import com.group_service.dto.*;
import com.group_service.entity.GroupMember;
import com.group_service.entity.StudentFreeTimeSlot;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.GroupMemberRole;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudentFreeTimeSlotRepository;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final StudentFreeTimeSlotRepository studentFreeTimeSlotRepository;

    @Override
    @Transactional
    public StudyGroupResponse createGroup(CreateStudyGroupRequest request) {
        String normalizedName = request.getName().trim();
        Long ownerUserId = request.getOwnerUserId();

        StudyGroup studyGroup = StudyGroup.builder()
                .name(normalizedName)
                .description(normalizeText(request.getDescription()))
                .ownerUserId(ownerUserId)
                .termId(request.getTermId())
                .mainSubjectId(request.getMainSubjectId())
                .subjectName(normalizeText(request.getSubjectName()))
                .studyGoal(normalizeText(request.getStudyGoal()))
                .studyMode(normalizeText(request.getStudyMode()))
                .maxMembers(request.getMaxMembers())
                .visibility(normalizeVisibility(request.getVisibility()))
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

        if (request.getFreeTimeSlots() != null && !request.getFreeTimeSlots().isEmpty()) {
            List<StudentFreeTimeSlot> slots = new ArrayList<>();

            for (FreeTimeSlotRequest slotRequest : request.getFreeTimeSlots()) {
                StudentFreeTimeSlot slot = StudentFreeTimeSlot.builder()
                        .groupId(savedStudyGroup.getId())
                        .termId(request.getTermId())
                        .dayOfWeek(slotRequest.getDayOfWeek())
                        .slotCode(slotRequest.getSlotCode())
                        .isAvailable(slotRequest.getIsAvailable() != null ? slotRequest.getIsAvailable() : true)
                        .build();

                slots.add(slot);
            }

            studentFreeTimeSlotRepository.saveAll(slots);
        }

        return toResponse(savedStudyGroup);
    }
    @Override
    public StudyGroupDetailResponse getGroupById(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        List<StudentFreeTimeSlot> freeTimeSlots = studentFreeTimeSlotRepository.findByGroupId(groupId);
        List<FreeTimeSlotResponse> slotResponses = freeTimeSlots.stream()
                .map(slot -> new FreeTimeSlotResponse(
                        slot.getId(),
                        slot.getGroupId(),
                        slot.getTermId(),
                        slot.getDayOfWeek(),
                        slot.getSlotCode(),
                        slot.getIsAvailable()
                ))
                .toList();

        return new StudyGroupDetailResponse(
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getDescription(),
                studyGroup.getOwnerUserId(),
                studyGroup.getTermId(),
                studyGroup.getMainSubjectId(),
                studyGroup.getSubjectName(),
                studyGroup.getStudyGoal(),
                studyGroup.getStudyMode(),
                studyGroup.getMaxMembers(),
                studyGroup.getVisibility(),
                studyGroup.getStatus(),
                studyGroup.getCreatedAt(),
                studyGroup.getUpdatedAt(),
                slotResponses
        );
    }

    private StudyGroupResponse toResponse(StudyGroup studyGroup) {
        return new StudyGroupResponse(
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getDescription(),
                studyGroup.getOwnerUserId(),
                studyGroup.getTermId(),
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

    private String normalizeVisibility(String value) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase() : "PRIVATE";
    }
}


