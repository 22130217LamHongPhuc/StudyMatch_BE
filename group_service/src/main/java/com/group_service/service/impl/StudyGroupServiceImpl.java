package com.group_service.service.impl;

import com.group_service.dto.*;

import com.group_service.dto.projection.AdminGroupProjection;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.GroupMember;
import com.group_service.entity.StudentFreeTimeSlot;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.*;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudentFreeTimeSlotRepository;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public StudyGroupResponse createStudyGroup(CreateStudyGroupRequest request) {
        String normalizedName = request.getName().trim();
        Long ownerUserId = request.getOwnerUserId();

        StudyGroup studyGroup = StudyGroup.builder()
                .groupType(GroupType.STUDY)
                .name(normalizedName)
                .description(normalizeText(request.getDescription()))
                .createdByUserId(request.getOwnerUserId())
                .ownerUserId(request.getOwnerUserId())
                .termId(request.getTermId())
                .mainSubjectId(request.getMainSubjectId())
                .subjectName(normalizeText(request.getSubjectName()))
                .maxMembers(request.getMaxMembers())
                .visibility(GroupVisibility.PUBLIC)
                .status(GroupStatus.ACTIVE)
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
    public StudyGroupResponse createCommunityGroup(CreateStudyGroupRequest request) {
        String normalizedName = request.getName().trim();
        Long ownerUserId = request.getOwnerUserId();

        StudyGroup studyGroup = StudyGroup.builder()
                .groupType(GroupType.COMMUNITY)
                .name(normalizedName)
                .description(normalizeText(request.getDescription()))
                .createdByUserId(request.getOwnerUserId())
                .ownerUserId(request.getOwnerUserId())
                .termId(request.getTermId())
                .mainSubjectId(request.getMainSubjectId())
                .subjectName(normalizeText(request.getSubjectName()))
                .visibility(GroupVisibility.COMMUNITY)
                .status(GroupStatus.ACTIVE)
                .build();

        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        GroupMember ownerMember = GroupMember.builder()
                .groupId(savedStudyGroup.getId())
                .userId(ownerUserId)
                .role(GroupMemberRole.ADMIN)
                .status(GroupMemberStatus.ACTIVE)
                .build();

        groupMemberRepository.save(ownerMember);

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
                studyGroup.getMaxMembers(),
                studyGroup.getVisibility(),
                studyGroup.getStatus(),
                studyGroup.getCreatedAt(),
                studyGroup.getUpdatedAt(),
                slotResponses
        );
    }


    @Override
    public List<StudyGroupDetailResponse> getGroupsByUserId(Long userId) {

        List<StudyGroup> groups = groupMemberRepository.findGroupsByUserId(
                userId,
                GroupMemberStatus.ACTIVE,
                GroupStatus.ACTIVE
        );

        return groups.stream()
                .map(this::mapToDetailResponse)
                .toList();
    }

    @Override
    public Page<AdminGroupResponse> getGroupsForAdmin(GroupFilterRequest filter,int page, int limit) {
        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AdminGroupProjection> groups = studyGroupRepository.filterAdminGroups(
                filter.getType(),
                filter.getStatus(),
                pageable
        );

        return groups.map(this::mapToAdminGroupResponse);
    }

    @Override
    public Page<AdminGroupResponse> getGroupsByKeywordForAdmin(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AdminGroupProjection> groups = studyGroupRepository.searchAdminGroupsByKeyword(
                keyword.trim(),
                pageable
        );

        System.out.println("Found " + groups.getTotalElements() + " groups matching keyword: " + keyword);

        return groups.map(this::mapToAdminGroupResponse);
    }


    private AdminGroupResponse mapToAdminGroupResponse(AdminGroupProjection group) {

        return AdminGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .type(group.getGroupType().name())
                .subjectName(group.getSubjectName())
                .status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .memberCount(group.getMemberCount())
                .build();
    }





    private StudyGroupDetailResponse mapToDetailResponse(StudyGroup group) {

        return new StudyGroupDetailResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getOwnerUserId(),
                group.getTermId(),
                group.getMainSubjectId(),
                group.getSubjectName(),

                group.getMaxMembers(),
                group.getVisibility(),
                group.getStatus(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                new ArrayList<>()
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

                studyGroup.getMaxMembers(),
                studyGroup.getVisibility(),
                studyGroup.getStatus(),
                studyGroup.getCreatedAt(),
                studyGroup.getUpdatedAt()
        );
    }


    @Override
    public GroupStats getStatsForGroups() {
        return studyGroupRepository.getStats();
    }



    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeVisibility(String value) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase() : "PRIVATE";
    }
}


