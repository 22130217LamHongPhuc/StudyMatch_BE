package com.group_service.service.impl;

import com.group_service.dto.*;

import com.group_service.dto.projection.AdminGroupProjection;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.GroupFreeTimeSlot;
import com.group_service.entity.GroupMember;
import com.group_service.entity.UserFreeTimeSlot;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.*;
import com.group_service.repository.GroupFreeTimeSlotRepository;
import com.group_service.repository.GroupMemberRepository;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupFreeTimeSlotRepository groupFreeTimeSlotRepository;
    private final com.group_service.repository.GroupInvitationRepository groupInvitationRepository;
    private final com.group_service.clients.UserClient userClient;
    private final com.group_service.clients.ChatClient chatClient;
    private StudyGroup studyGroup;

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

        addInvitedMembers(savedStudyGroup.getId(), ownerUserId, request.getMaxMembers(), request.getInvitedUserIds());

        if (request.getFreeTimeSlots() != null && !request.getFreeTimeSlots().isEmpty()) {
            List<GroupFreeTimeSlot> slots = new ArrayList<>();

            for (FreeTimeSlotRequest slotRequest : request.getFreeTimeSlots()) {
                GroupFreeTimeSlot slot = GroupFreeTimeSlot.builder()
                        .groupId(savedStudyGroup.getId())
                        .termId(request.getTermId())
                        .dayOfWeek(slotRequest.getDayOfWeek())
                        .slotCode(slotRequest.getSlotCode())
                        .isAvailable(slotRequest.getIsAvailable() != null ? slotRequest.getIsAvailable() : true)
                        .build();

                slots.add(slot);
            }

            groupFreeTimeSlotRepository.saveAll(slots);
        }

        return toResponse(savedStudyGroup,false);
    }

    @Override
    @Transactional
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

        addInvitedMembers(savedStudyGroup.getId(), ownerUserId, request.getMaxMembers(), request.getInvitedUserIds());

        return toResponse(savedStudyGroup,false);
    }

    @Override
    public StudyGroupDetailResponse getGroupById(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        List<GroupFreeTimeSlot> freeTimeSlots = groupFreeTimeSlotRepository.findByGroupId(groupId);
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

        String keyword = filter.getKeyword() == null
                ? null
                : filter.getKeyword().trim();

        Page<AdminGroupProjection> groups = studyGroupRepository.filterAdminGroups(
                filter.getType(),
                filter.getStatus(),
                keyword,
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

    @Override
    public Page<StudyGroupResponse> getGroupsByTypeAndSubject(GroupType groupType, Long mainSubjectId,Long currentUserId, int page, int limit) {
        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return studyGroupRepository.findByFiltersForBrowse(
                GroupStatus.ACTIVE,
                groupType,
                mainSubjectId,
                currentUserId,
                List.of(GroupMemberStatus.ACTIVE),
                pageable
        );
    }



    @Override
    @Transactional
    public JoinGroupResponse joinGroup(Long groupId, JoinGroupRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must not be null");
        }

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        if (studyGroup.getStatus() == GroupStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot join a deleted group");
        }

        if (studyGroup.getStatus() != GroupStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group is not active");
        }

        Long userId = request.getUserId();
        var existingMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (existingMember.isPresent() && existingMember.get().getStatus() == GroupMemberStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already joined this group");
        }

        Integer maxMembers = studyGroup.getMaxMembers();
        if (maxMembers != null) {
            long activeMembers = groupMemberRepository.countByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE);
            if (activeMembers >= maxMembers) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group has reached max members");
            }
        }

        if (existingMember.isPresent()) {
            GroupMember member = existingMember.get();
            member.setRole(GroupMemberRole.MEMBER);
            member.setStatus(GroupMemberStatus.ACTIVE);
            GroupMember saved = groupMemberRepository.save(member);

            try {
                chatClient.syncGroupParticipants(groupId);
            } catch (Exception e) {
                System.err.println("Failed to sync group participants on joinGroup: " + e.getMessage());
            }

            return new JoinGroupResponse(
                    saved.getId(),
                    saved.getGroupId(),
                    saved.getUserId(),
                    saved.getRole(),
                    saved.getStatus(),
                    saved.getJoinedAt()
            );
        }

        GroupMember member = GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role(GroupMemberRole.MEMBER)
                .status(GroupMemberStatus.ACTIVE)
                .build();

        GroupMember saved = groupMemberRepository.save(member);

        try {
            chatClient.syncGroupParticipants(groupId);
        } catch (Exception e) {
            System.err.println("Failed to sync group participants on joinGroup: " + e.getMessage());
        }

        return new JoinGroupResponse(
                saved.getId(),
                saved.getGroupId(),
                saved.getUserId(),
                saved.getRole(),
                saved.getStatus(),
                saved.getJoinedAt()
        );
    }

    private AdminGroupResponse mapToAdminGroupResponse(AdminGroupProjection group) {
        return AdminGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .type(group.getGroupType().name())
                .visibility(group.getVisibility() != null ? group.getVisibility().name() : null)
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
                new java.util.ArrayList<>()
        );
    }

    private StudyGroupResponse toResponse(StudyGroup studyGroup,boolean isMember) {
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
                studyGroup.getUpdatedAt(),
                isMember
        );
    }

    private void addInvitedMembers(Long groupId, Long ownerUserId, Integer maxMembers, List<Long> invitedUserIds) {
        if (invitedUserIds == null || invitedUserIds.isEmpty()) {
            return;
        }

        List<Long> uniqueInvitedUserIds = invitedUserIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .filter(userId -> !userId.equals(ownerUserId))
                .toList();

        if (maxMembers != null) {
            long totalMembersAfterCreate = 1L + uniqueInvitedUserIds.size();
            if (totalMembersAfterCreate > maxMembers) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invited members exceed max members");
            }
        }

        if (uniqueInvitedUserIds.isEmpty()) {
            return;
        }

        StudyGroup studyGroup = studyGroupRepository.findById(groupId).orElse(null);
        String groupName = studyGroup != null ? studyGroup.getName() : "Nhóm học";

        String ownerName = "Trưởng nhóm";
        try {
            Map<String, Object> userData = userClient.getUserById(ownerUserId);
            if (userData != null) {
                if (userData.containsKey("fullName")) {
                    ownerName = (String) userData.get("fullName");
                } else if (userData.containsKey("full_name")) {
                    ownerName = (String) userData.get("full_name");
                }
            }
        } catch (Exception e) {
            // ignore
        }

        List<com.group_service.entity.GroupInvitation> invitations = new ArrayList<>();
        for (Long userId : uniqueInvitedUserIds) {
            invitations.add(com.group_service.entity.GroupInvitation.builder()
                    .groupId(groupId)
                    .inviterUserId(ownerUserId)
                    .inviteeUserId(userId)
                    .status(GroupInvitationStatus.PENDING)
                    .build());
        }
        List<com.group_service.entity.GroupInvitation> savedInvitations = groupInvitationRepository.saveAll(invitations);

        for (com.group_service.entity.GroupInvitation inv : savedInvitations) {
            try {
                chatClient.sendGroupInvitationNotification(com.group_service.dto.GroupInvitationNotificationRequest.builder()
                        .userId(inv.getInviteeUserId())
                        .groupId(groupId)
                        .groupName(groupName)
                        .inviterName(ownerName)
                        .invitationId(inv.getId())
                        .build());
            } catch (Exception e) {
                System.err.println("Failed to send WebSocket notification on group creation: " + e.getMessage());
            }
        }
    }

    @Override
    public GroupStats getStatsForGroups() {
        return studyGroupRepository.getStats();
    }

    @Override
    public AdminGroupDetailResponse getGroupDetailForAdmin(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        long memberCount = groupMemberRepository.countByGroupId(groupId);

        List<GroupFreeTimeSlot> freeTimeSlots = groupFreeTimeSlotRepository.findByGroupId(groupId);
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

        return new AdminGroupDetailResponse(
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getDescription(),
                studyGroup.getGroupType(),
                studyGroup.getCreatedByUserId(),
                studyGroup.getOwnerUserId(),
                studyGroup.getTermId(),
                studyGroup.getMainSubjectId(),
                studyGroup.getSubjectName(),
                studyGroup.getMaxMembers(),
                studyGroup.getVisibility(),
                studyGroup.getStatus(),
                memberCount,
                studyGroup.getCreatedAt(),
                studyGroup.getUpdatedAt(),
                slotResponses
        );
    }

    @Override
    @Transactional
    public AdminGroupDetailResponse updateGroupStatusForAdmin(Long groupId, UpdateGroupStatusRequest request) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        GroupStatus currentStatus = studyGroup.getStatus();
        GroupStatus newStatus = request.getStatus();

        if (currentStatus == GroupStatus.DELETED && newStatus != GroupStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status of a deleted group");
        }

        if (currentStatus == newStatus) {
            return getGroupDetailForAdmin(groupId);
        }

        studyGroup.setStatus(newStatus);
        studyGroupRepository.save(studyGroup);

        return getGroupDetailForAdmin(groupId);
    }

    @Override
    @Transactional
    public com.group_service.dto.GroupInvitationResponse sendInvitation(Long groupId, Long inviteeUserId, String token) {
        TokenValidateResponse valRes = userClient.validateToken(token);
        if (valRes == null || !valRes.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Long inviterUserId = valRes.getUserId();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        Optional<GroupMember> inviterMemberOpt = groupMemberRepository.findByGroupIdAndUserId(groupId, inviterUserId);
        if (inviterMemberOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this group");
        }
        GroupMember inviterMember = inviterMemberOpt.get();
        if (inviterMember.getRole() != GroupMemberRole.OWNER && inviterMember.getRole() != GroupMemberRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner or admin can invite members");
        }

        boolean isAlreadyActive = groupMemberRepository.existsByGroupIdAndUserIdAndStatus(groupId, inviteeUserId, GroupMemberStatus.ACTIVE);
        if (isAlreadyActive) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already an active member of this group");
        }

        boolean hasPending = groupInvitationRepository.existsByGroupIdAndInviteeUserIdAndStatus(groupId, inviteeUserId, GroupInvitationStatus.PENDING);
        if (hasPending) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An invitation is already pending for this user");
        }

        com.group_service.entity.GroupInvitation invitation = com.group_service.entity.GroupInvitation.builder()
                .groupId(groupId)
                .inviterUserId(inviterUserId)
                .inviteeUserId(inviteeUserId)
                .status(GroupInvitationStatus.PENDING)
                .build();
        com.group_service.entity.GroupInvitation saved = groupInvitationRepository.save(invitation);

        String inviterName = valRes.getUsername();
        try {
            Map<String, Object> userData = userClient.getUserById(inviterUserId);
            if (userData != null) {
                if (userData.containsKey("fullName")) {
                    inviterName = (String) userData.get("fullName");
                } else if (userData.containsKey("full_name")) {
                    inviterName = (String) userData.get("full_name");
                }
            }
        } catch (Exception e) {
            // ignore
        }

        try {
            chatClient.sendGroupInvitationNotification(com.group_service.dto.GroupInvitationNotificationRequest.builder()
                    .userId(inviteeUserId)
                    .groupId(groupId)
                    .groupName(studyGroup.getName())
                    .inviterName(inviterName)
                    .invitationId(saved.getId())
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket notification: " + e.getMessage());
        }

        return com.group_service.dto.GroupInvitationResponse.builder()
                .invitationId(saved.getId())
                .groupId(groupId)
                .groupName(studyGroup.getName())
                .inviterUserId(inviterUserId)
                .inviteeUserId(inviteeUserId)
                .inviterName(inviterName)
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<com.group_service.dto.GroupInvitationResponse> getPendingInvitations(String token) {
        TokenValidateResponse valRes = userClient.validateToken(token);
        if (valRes == null || !valRes.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Long inviteeUserId = valRes.getUserId();

        List<com.group_service.entity.GroupInvitation> invitations = groupInvitationRepository
                .findByInviteeUserIdAndStatus(inviteeUserId, GroupInvitationStatus.PENDING);

        List<com.group_service.dto.GroupInvitationResponse> responses = new ArrayList<>();
        for (com.group_service.entity.GroupInvitation inv : invitations) {
            StudyGroup group = studyGroupRepository.findById(inv.getGroupId()).orElse(null);
            String groupName = group != null ? group.getName() : "Unknown Group";

            String inviterName = "User #" + inv.getInviterUserId();
            String inviterAvatar = null;
            try {
                Map<String, Object> userData = userClient.getUserById(inv.getInviterUserId());
                if (userData != null) {
                    if (userData.containsKey("fullName")) {
                        inviterName = (String) userData.get("fullName");
                    } else if (userData.containsKey("full_name")) {
                        inviterName = (String) userData.get("full_name");
                    }
                    if (userData.containsKey("avatarUrl")) {
                        inviterAvatar = (String) userData.get("avatarUrl");
                    } else if (userData.containsKey("avatar_url")) {
                        inviterAvatar = (String) userData.get("avatar_url");
                    }
                }
            } catch (Exception e) {
                // ignore
            }

            responses.add(com.group_service.dto.GroupInvitationResponse.builder()
                    .invitationId(inv.getId())
                    .groupId(inv.getGroupId())
                    .groupName(groupName)
                    .inviterUserId(inv.getInviterUserId())
                    .inviteeUserId(inv.getInviteeUserId())
                    .inviterName(inviterName)
                    .inviterAvatar(inviterAvatar)
                    .status(inv.getStatus())
                    .createdAt(inv.getCreatedAt())
                    .build());
        }
        return responses;
    }

    @Override
    public List<com.group_service.dto.GroupInvitationResponse> getGroupInvitations(Long groupId, String token) {
        TokenValidateResponse valRes = userClient.validateToken(token);
        if (valRes == null || !valRes.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Long requesterUserId = valRes.getUserId();

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        GroupMember requesterMember = groupMemberRepository.findByGroupIdAndUserId(groupId, requesterUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this group"));

        if (requesterMember.getRole() != GroupMemberRole.OWNER && requesterMember.getRole() != GroupMemberRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner or admin can view invitations");
        }

        return groupInvitationRepository.findByGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(inv -> com.group_service.dto.GroupInvitationResponse.builder()
                        .invitationId(inv.getId())
                        .groupId(inv.getGroupId())
                        .groupName(group.getName())
                        .inviterUserId(inv.getInviterUserId())
                        .inviteeUserId(inv.getInviteeUserId())
                        .status(inv.getStatus())
                        .createdAt(inv.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void acceptInvitation(Long invitationId, String token) {
        TokenValidateResponse valRes = userClient.validateToken(token);
        if (valRes == null || !valRes.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Long inviteeUserId = valRes.getUserId();

        com.group_service.entity.GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

        if (!invitation.getInviteeUserId().equals(inviteeUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This invitation is not for you");
        }

        if (invitation.getStatus() != GroupInvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation is not in pending status");
        }

        StudyGroup studyGroup = studyGroupRepository.findById(invitation.getGroupId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));

        Integer maxMembers = studyGroup.getMaxMembers();
        if (maxMembers != null) {
            long activeMembers = groupMemberRepository.countByGroupIdAndStatus(invitation.getGroupId(), GroupMemberStatus.ACTIVE);
            if (activeMembers >= maxMembers) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group has reached max members");
            }
        }

        invitation.setStatus(GroupInvitationStatus.ACCEPTED);
        invitation.setRespondedAt(java.time.LocalDateTime.now());
        groupInvitationRepository.save(invitation);

        Optional<GroupMember> existingMemberOpt = groupMemberRepository.findByGroupIdAndUserId(invitation.getGroupId(), inviteeUserId);
        if (existingMemberOpt.isPresent()) {
            GroupMember member = existingMemberOpt.get();
            member.setRole(GroupMemberRole.MEMBER);
            member.setStatus(GroupMemberStatus.ACTIVE);
            groupMemberRepository.save(member);
        } else {
            GroupMember member = GroupMember.builder()
                    .groupId(invitation.getGroupId())
                    .userId(inviteeUserId)
                    .role(GroupMemberRole.MEMBER)
                    .status(GroupMemberStatus.ACTIVE)
                    .build();
            groupMemberRepository.save(member);
        }

        try {
            chatClient.syncGroupParticipants(invitation.getGroupId());
        } catch (Exception e) {
            System.err.println("Failed to sync group participants on acceptInvitation: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void rejectInvitation(Long invitationId, String token) {
        TokenValidateResponse valRes = userClient.validateToken(token);
        if (valRes == null || !valRes.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Long inviteeUserId = valRes.getUserId();

        com.group_service.entity.GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

        if (!invitation.getInviteeUserId().equals(inviteeUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This invitation is not for you");
        }

        if (invitation.getStatus() != GroupInvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation is not in pending status");
        }

        invitation.setStatus(GroupInvitationStatus.REJECTED);
        invitation.setRespondedAt(java.time.LocalDateTime.now());
        groupInvitationRepository.save(invitation);

        StudyGroup group = studyGroupRepository.findById(invitation.getGroupId()).orElse(null);
        try {
            chatClient.sendGroupInvitationStatusNotification(com.group_service.dto.GroupInvitationNotificationRequest.builder()
                    .userId(invitation.getInviterUserId())
                    .groupId(invitation.getGroupId())
                    .groupName(group != null ? group.getName() : null)
                    .invitationId(invitation.getId())
                    .inviteeUserId(invitation.getInviteeUserId())
                    .status(invitation.getStatus().name())
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket notification on rejectInvitation: " + e.getMessage());
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeVisibility(String value) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase() : "PRIVATE";
    }
}

