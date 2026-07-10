package com.group_service.service.impl;

import com.group_service.clients.ChatClient;
import com.group_service.clients.UserClient;
import com.group_service.dto.AdminGroupDetailResponse;
import com.group_service.dto.AdminGroupResponse;
import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.FreeTimeSlotRequest;
import com.group_service.dto.FreeTimeSlotResponse;
import com.group_service.dto.CommonGroupResponse;
import com.group_service.dto.GroupFilterRequest;
import com.group_service.dto.GroupInvitationNotificationRequest;
import com.group_service.dto.GroupInvitationResponse;
import com.group_service.dto.JoinGroupRequest;
import com.group_service.dto.StudyGroupDetailResponse;
import com.group_service.dto.StudyGroupResponse;
import com.group_service.dto.TokenValidateResponse;
import com.group_service.dto.UserGroupStatsResponse;
import com.group_service.dto.UpdateGroupStatusRequest;
import com.group_service.dto.projection.AdminGroupProjection;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.GroupFreeTimeSlot;
import com.group_service.entity.GroupInvitation;
import com.group_service.entity.GroupMember;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.GroupInvitationStatus;
import com.group_service.entity.enums.GroupMemberRole;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.entity.enums.GroupVisibility;
import com.group_service.repository.GroupFreeTimeSlotRepository;
import com.group_service.repository.GroupInvitationRepository;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.service.CloudinaryService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private final GroupInvitationRepository groupInvitationRepository;
    private final UserClient userClient;
    private final ChatClient chatClient;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public StudyGroupResponse createStudyGroup(CreateStudyGroupRequest request, MultipartFile avatar) {
        uploadAvatarIfPresent(request, avatar);

        String normalizedName = requireName(request.getName());
        Long ownerUserId = request.getOwnerUserId();

        StudyGroup studyGroup = StudyGroup.builder()
                .groupType(GroupType.STUDY)
                .name(normalizedName)
                .avatarUrl(normalizeText(request.getAvatarUrl()))
                .description(normalizeText(request.getDescription()))
                .createdByUserId(ownerUserId)
                .ownerUserId(ownerUserId)
                .termId(request.getTermId())
                .mainSubjectId(request.getMainSubjectId())
                .subjectName(normalizeText(request.getSubjectName()))
                .maxMembers(request.getMaxMembers())
                .visibility(resolveStudyVisibility(request.getVisibility()))
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
        saveFreeTimeSlots(savedStudyGroup.getId(), request.getTermId(), request.getFreeTimeSlots());

        return toResponse(savedStudyGroup, false);
    }

    @Override
    @Transactional
    public StudyGroupResponse createCommunityGroup(CreateStudyGroupRequest request, MultipartFile avatar) {
        uploadAvatarIfPresent(request, avatar);

        String normalizedName = requireName(request.getName());
        Long ownerUserId = request.getOwnerUserId();

        StudyGroup studyGroup = StudyGroup.builder()
                .groupType(GroupType.COMMUNITY)
                .name(normalizedName)
                .avatarUrl(normalizeText(request.getAvatarUrl()))
                .description(normalizeText(request.getDescription()))
                .createdByUserId(ownerUserId)
                .ownerUserId(ownerUserId)
                .termId(request.getTermId())
                .mainSubjectId(request.getMainSubjectId())
                .subjectName(normalizeText(request.getSubjectName()))
                .maxMembers(request.getMaxMembers())
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

        return toResponse(savedStudyGroup, false);
    }

    @Override
    public StudyGroupDetailResponse getGroupById(Long groupId) {
        StudyGroup studyGroup = findGroupOrThrow(groupId);
        return mapToDetailResponse(studyGroup, getGroupFreeTimeSlotResponses(groupId));
    }

    @Override
    public List<StudyGroupDetailResponse> getGroupsByUserId(Long userId) {
        List<StudyGroup> groups = groupMemberRepository.findGroupsByUserId(
                userId,
                GroupMemberStatus.ACTIVE,
                GroupStatus.ACTIVE);

        return groups.stream()
                .map(this::mapToDetailResponse)
                .toList();
    }

    @Override
    public Page<AdminGroupResponse> getGroupsForAdmin(GroupFilterRequest filter, int page, int limit) {
        GroupFilterRequest effectiveFilter = filter == null ? new GroupFilterRequest() : filter;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        String keyword = normalizeText(effectiveFilter.getKeyword());
        Page<AdminGroupProjection> groups = studyGroupRepository.filterAdminGroups(
                effectiveFilter.getType(),
                effectiveFilter.getStatus(),
                keyword,
                pageable);

        return groups.map(this::mapToAdminGroupResponse);
    }

    @Override
    public Page<AdminGroupResponse> getGroupsByKeywordForAdmin(String keyword, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        String normalizedKeyword = normalizeText(keyword);
        if (normalizedKeyword == null) {
            return Page.empty(pageable);
        }

        Page<AdminGroupProjection> groups = studyGroupRepository.searchAdminGroupsByKeyword(
                normalizedKeyword,
                pageable);

        return groups.map(this::mapToAdminGroupResponse);
    }

    @Override
    public GroupStats getStatsForGroups() {
        return studyGroupRepository.getStats();
    }

    @Override
    public AdminGroupDetailResponse getGroupDetailForAdmin(Long groupId) {
        StudyGroup studyGroup = findGroupOrThrow(groupId);
        long memberCount = groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE)
                .stream()
                .filter(member -> member.getRole() != GroupMemberRole.ADMIN)
                .count();

        return new AdminGroupDetailResponse(
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getAvatarUrl(),
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
                getGroupFreeTimeSlotResponses(groupId));
    }

    @Override
    @Transactional
    public AdminGroupDetailResponse updateGroupStatusForAdmin(Long groupId, UpdateGroupStatusRequest request) {
        StudyGroup studyGroup = findGroupOrThrow(groupId);
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
    public Page<StudyGroupResponse> getGroupsByTypeAndSubject(
            GroupType groupType,
            Long mainSubjectId,
            Long currentUserId,
            int page,
            int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        return studyGroupRepository.findByFiltersForBrowse(
                GroupStatus.ACTIVE,
                groupType,
                mainSubjectId,
                currentUserId,
                List.of(GroupMemberStatus.ACTIVE),
                GroupMemberStatus.ACTIVE,
                GroupMemberRole.ADMIN,
                pageable);
    }

    @Override
    @Transactional
    public GroupInvitationResponse joinGroup(Long groupId, JoinGroupRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must not be null");
        }

        StudyGroup studyGroup = findGroupOrThrow(groupId);
        if (studyGroup.getStatus() == GroupStatus.DELETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot join a deleted group");
        }
        if (studyGroup.getStatus() != GroupStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group is not active");
        }

        Long userId = request.getUserId();
        boolean isAlreadyActive = groupMemberRepository.existsByGroupIdAndUserIdAndStatus(
                groupId,
                userId,
                GroupMemberStatus.ACTIVE);
        if (isAlreadyActive) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already joined this group");
        }

        boolean hasPending = groupInvitationRepository.existsByGroupIdAndInviteeUserIdAndStatus(
                groupId,
                userId,
                GroupInvitationStatus.PENDING);
        if (hasPending) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A join request is already pending for this group");
        }

        Long ownerUserId = studyGroup.getOwnerUserId();
        GroupInvitation savedInvitation = groupInvitationRepository.save(GroupInvitation.builder()
                .groupId(groupId)
                .inviterUserId(userId)
                .inviteeUserId(userId)
                .message(request.getMessage())
                .status(GroupInvitationStatus.PENDING)
                .build());

        String requesterName = resolveUserName(userId, "User #" + userId);
        groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE)
                .stream()
                .filter(member -> member.getRole() == GroupMemberRole.OWNER
                        || member.getRole() == GroupMemberRole.ADMIN)
                .map(GroupMember::getUserId)
                .distinct()
                .forEach(moderatorUserId -> {
                    try {
                        chatClient.sendGroupInvitationNotification(GroupInvitationNotificationRequest.builder()
                                .userId(moderatorUserId)
                                .groupId(groupId)
                                .groupName(studyGroup.getName())
                                .inviterName(requesterName)
                                .invitationId(savedInvitation.getId())
                                .inviteeUserId(userId)
                                .build());
                    } catch (Exception e) {
                        System.err.println("Failed to send WebSocket join request notification: " + e.getMessage());
                    }
                });

        return GroupInvitationResponse.builder()
                .invitationId(savedInvitation.getId())
                .groupId(groupId)
                .groupName(studyGroup.getName())
                .groupAvatarUrl(studyGroup.getAvatarUrl())
                .inviterUserId(savedInvitation.getInviterUserId())
                .inviteeUserId(savedInvitation.getInviteeUserId())
                .message(savedInvitation.getMessage())
                .status(savedInvitation.getStatus())
                .createdAt(savedInvitation.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public GroupInvitationResponse sendInvitation(Long groupId, Long inviteeUserId, String message, String token) {
        TokenValidateResponse validation = validateTokenOrThrow(token);
        Long inviterUserId = validation.getUserId();

        StudyGroup studyGroup = findGroupOrThrow(groupId);
        GroupMember inviterMember = groupMemberRepository.findByGroupIdAndUserId(groupId, inviterUserId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this group"));

        if (inviterMember.getRole() != GroupMemberRole.OWNER && inviterMember.getRole() != GroupMemberRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner or admin can invite members");
        }

        boolean isAlreadyActive = groupMemberRepository.existsByGroupIdAndUserIdAndStatus(
                groupId,
                inviteeUserId,
                GroupMemberStatus.ACTIVE);
        if (isAlreadyActive) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already an active member of this group");
        }

        boolean hasPending = groupInvitationRepository.existsByGroupIdAndInviteeUserIdAndStatus(
                groupId,
                inviteeUserId,
                GroupInvitationStatus.PENDING);
        if (hasPending) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An invitation is already pending for this user");
        }

        GroupInvitation savedInvitation = groupInvitationRepository.save(GroupInvitation.builder()
                .groupId(groupId)
                .inviterUserId(inviterUserId)
                .inviteeUserId(inviteeUserId)
                .message(message)
                .status(GroupInvitationStatus.PENDING)
                .build());

        String inviterName = resolveUserName(inviterUserId, validation.getUsername());
        try {
            chatClient.sendGroupInvitationNotification(GroupInvitationNotificationRequest.builder()
                    .userId(inviteeUserId)
                    .groupId(groupId)
                    .groupName(studyGroup.getName())
                    .inviterName(inviterName)
                    .invitationId(savedInvitation.getId())
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket notification: " + e.getMessage());
        }

        return GroupInvitationResponse.builder()
                .invitationId(savedInvitation.getId())
                .groupId(groupId)
                .groupName(studyGroup.getName())
                .groupAvatarUrl(studyGroup.getAvatarUrl())
                .inviterUserId(inviterUserId)
                .inviteeUserId(inviteeUserId)
                .inviterUserId(savedInvitation.getInviterUserId())
                .inviteeUserId(savedInvitation.getInviteeUserId())
                .message(savedInvitation.getMessage())
                .inviterName(inviterName)
                .status(savedInvitation.getStatus())
                .createdAt(savedInvitation.getCreatedAt())
                .build();
    }

    @Override
    public List<GroupInvitationResponse> getPendingInvitations(String token) {
        Long inviteeUserId = validateTokenOrThrow(token).getUserId();

        List<GroupInvitation> invitations = groupInvitationRepository.findByInviteeUserIdAndStatus(
                inviteeUserId,
                GroupInvitationStatus.PENDING);

        List<GroupInvitationResponse> responses = new ArrayList<>();
        for (GroupInvitation invitation : invitations) {
            if (invitation.getInviterUserId().equals(invitation.getInviteeUserId())) {
                continue;
            }
            StudyGroup group = studyGroupRepository.findById(invitation.getGroupId()).orElse(null);
            String inviterName = "User #" + invitation.getInviterUserId();
            String inviterAvatar = null;

            try {
                Map<String, Object> userData = userClient.getUserById(invitation.getInviterUserId());
                if (userData != null) {
                    inviterName = getStringValue(userData, inviterName, "fullName", "full_name");
                    inviterAvatar = getStringValue(userData, inviterAvatar, "avatarUrl", "avatar_url");
                }
            } catch (Exception e) {
            }

            responses.add(GroupInvitationResponse.builder()
                    .invitationId(invitation.getId())
                    .groupId(invitation.getGroupId())
                    .groupName(group != null ? group.getName() : "Unknown Group")
                    .groupAvatarUrl(group != null ? group.getAvatarUrl() : null)
                    .inviterUserId(invitation.getInviterUserId())
                    .inviteeUserId(invitation.getInviteeUserId())
                    .inviterName(inviterName)
                    .message(invitation.getMessage())
                    .status(invitation.getStatus())
                    .createdAt(invitation.getCreatedAt())
                    .build());
        }

        return responses;
    }

    @Override
    public List<GroupInvitationResponse> getGroupInvitations(Long groupId, String token) {
        Long requesterUserId = validateTokenOrThrow(token).getUserId();
        StudyGroup group = findGroupOrThrow(groupId);

        GroupMember requesterMember = groupMemberRepository.findByGroupIdAndUserId(groupId, requesterUserId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this group"));

        if (requesterMember.getRole() != GroupMemberRole.OWNER && requesterMember.getRole() != GroupMemberRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner or admin can view invitations");
        }

        return groupInvitationRepository.findByGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(invitation -> GroupInvitationResponse.builder()
                        .invitationId(invitation.getId())
                        .groupId(invitation.getGroupId())
                        .groupName(group.getName())
                        .groupAvatarUrl(group.getAvatarUrl())
                        .inviterUserId(invitation.getInviterUserId())
                        .inviteeUserId(invitation.getInviteeUserId())
                        .message(invitation.getMessage())
                        .status(invitation.getStatus())
                        .createdAt(invitation.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void acceptInvitation(Long invitationId, String token) {
        Long actorUserId = validateTokenOrThrow(token).getUserId();
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

        boolean isInvitationToActor = invitation.getInviteeUserId().equals(actorUserId)
                && !invitation.getInviterUserId().equals(actorUserId);
        if (!isInvitationToActor) {
            assertCanModerateInvitation(invitation, actorUserId);
        }
        if (invitation.getStatus() != GroupInvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation is not in pending status");
        }

        StudyGroup studyGroup = findGroupOrThrow(invitation.getGroupId());
        Integer maxMembers = studyGroup.getMaxMembers();
        if (maxMembers != null) {
            long activeMembers = groupMemberRepository.countByGroupIdAndStatus(
                    invitation.getGroupId(),
                    GroupMemberStatus.ACTIVE);
            if (activeMembers >= maxMembers) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group has reached max members");
            }
        }

        invitation.setStatus(GroupInvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        groupInvitationRepository.save(invitation);

        Optional<GroupMember> existingMember = groupMemberRepository.findByGroupIdAndUserId(
                invitation.getGroupId(),
                invitation.getInviteeUserId());
        if (existingMember.isPresent()) {
            GroupMember member = existingMember.get();
            member.setRole(GroupMemberRole.MEMBER);
            member.setStatus(GroupMemberStatus.ACTIVE);
            groupMemberRepository.save(member);
        } else {
            groupMemberRepository.save(GroupMember.builder()
                    .groupId(invitation.getGroupId())
                    .userId(invitation.getInviteeUserId())
                    .role(GroupMemberRole.MEMBER)
                    .status(GroupMemberStatus.ACTIVE)
                    .build());
        }

        trySyncGroupParticipants(invitation.getGroupId(), "acceptInvitation");
    }

    @Override
    @Transactional
    public void rejectInvitation(Long invitationId, String token) {
        Long actorUserId = validateTokenOrThrow(token).getUserId();
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));

        boolean isInvitationToActor = invitation.getInviteeUserId().equals(actorUserId)
                && !invitation.getInviterUserId().equals(actorUserId);
        if (!isInvitationToActor) {
            assertCanModerateInvitation(invitation, actorUserId);
        }
        if (invitation.getStatus() != GroupInvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation is not in pending status");
        }

        invitation.setStatus(GroupInvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
        groupInvitationRepository.save(invitation);

        StudyGroup group = studyGroupRepository.findById(invitation.getGroupId()).orElse(null);
        try {
            chatClient.sendGroupInvitationStatusNotification(GroupInvitationNotificationRequest.builder()
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

    @Override
    @Transactional(readOnly = true)
    public UserGroupStatsResponse getCurrentUserGroupStats(Long userId) {
        long joinedGroupCount = groupMemberRepository.countGroupsByUserId(
                userId,
                GroupMemberStatus.ACTIVE,
                GroupStatus.ACTIVE);
        long pendingInvitationCount = groupInvitationRepository.findByInviteeUserIdAndStatus(
                userId,
                GroupInvitationStatus.PENDING).stream()
                .filter(invitation -> !invitation.getInviterUserId().equals(invitation.getInviteeUserId()))
                .count();

        return new UserGroupStatsResponse(joinedGroupCount, pendingInvitationCount);
    }

    private void assertCanModerateInvitation(GroupInvitation invitation, Long actorUserId) {
        GroupMember moderator = groupMemberRepository.findByGroupIdAndUserIdAndStatus(
                invitation.getGroupId(),
                actorUserId,
                GroupMemberStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only group owner or admin can respond to this request"));

        if (moderator.getRole() != GroupMemberRole.OWNER && moderator.getRole() != GroupMemberRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only group owner or admin can respond to this request");
        }
    }

    private StudyGroup findGroupOrThrow(Long groupId) {
        return studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study group not found"));
    }

    private void uploadAvatarIfPresent(CreateStudyGroupRequest request, MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            return;
        }

        try {
            request.setAvatarUrl(cloudinaryService.uploadFile(avatar));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload avatar", e);
        }
    }

    private void saveFreeTimeSlots(Long groupId, Long termId, List<FreeTimeSlotRequest> slotRequests) {
        if (slotRequests == null || slotRequests.isEmpty()) {
            return;
        }
        if (termId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "termId is required when free time slots are provided");
        }

        List<GroupFreeTimeSlot> slots = slotRequests.stream()
                .map(slotRequest -> GroupFreeTimeSlot.builder()
                        .groupId(groupId)
                        .termId(termId)
                        .dayOfWeek(slotRequest.getDayOfWeek())
                        .slotCode(slotRequest.getSlotCode())
                        .isAvailable(slotRequest.getIsAvailable() != null ? slotRequest.getIsAvailable() : Boolean.TRUE)
                        .build())
                .toList();

        groupFreeTimeSlotRepository.saveAll(slots);
    }

    private List<FreeTimeSlotResponse> getGroupFreeTimeSlotResponses(Long groupId) {
        return groupFreeTimeSlotRepository.findByGroupId(groupId)
                .stream()
                .map(slot -> new FreeTimeSlotResponse(
                        slot.getId(),
                        slot.getGroupId(),
                        slot.getTermId(),
                        slot.getDayOfWeek(),
                        slot.getSlotCode(),
                        slot.getIsAvailable()))
                .toList();
    }

    private AdminGroupResponse mapToAdminGroupResponse(AdminGroupProjection group) {
        return AdminGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .avatarUrl(group.getAvatarUrl())
                .type(group.getGroupType().name())
                .visibility(group.getVisibility() != null ? group.getVisibility().name() : null)
                .subjectName(group.getSubjectName())
                .status(group.getStatus())
                .createdAt(group.getCreatedAt())
                .memberCount(group.getMemberCount())
                .build();
    }

    private StudyGroupDetailResponse mapToDetailResponse(StudyGroup group) {
        return mapToDetailResponse(group, List.of());
    }

    private StudyGroupDetailResponse mapToDetailResponse(StudyGroup group, List<FreeTimeSlotResponse> freeTimeSlots) {
        return new StudyGroupDetailResponse(
                group.getId(),
                group.getName(),
                group.getAvatarUrl(),
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
                freeTimeSlots);
    }

    private StudyGroupResponse toResponse(StudyGroup studyGroup, boolean isMember) {
        return new StudyGroupResponse(
                studyGroup.getId(),
                studyGroup.getName(),
                studyGroup.getAvatarUrl(),
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
                1L,
                isMember,
                false);
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

        if (uniqueInvitedUserIds.isEmpty()) {
            return;
        }

        if (maxMembers != null) {
            long totalMembersAfterCreate = 1L + uniqueInvitedUserIds.size();
            if (totalMembersAfterCreate > maxMembers) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invited members exceed max members");
            }
        }

        StudyGroup studyGroup = studyGroupRepository.findById(groupId).orElse(null);
        String groupName = studyGroup != null ? studyGroup.getName() : "Study group";
        String ownerName = resolveUserName(ownerUserId, "Group owner");

        List<GroupInvitation> invitations = uniqueInvitedUserIds.stream()
                .map(userId -> GroupInvitation.builder()
                        .groupId(groupId)
                        .inviterUserId(userId)
                        .inviteeUserId(userId)
                        .status(GroupInvitationStatus.PENDING)
                        .build())
                .toList();

        List<GroupInvitation> savedInvitations = groupInvitationRepository.saveAll(invitations);
        for (GroupInvitation invitation : savedInvitations) {
            try {
                chatClient.sendGroupInvitationNotification(GroupInvitationNotificationRequest.builder()
                        .userId(invitation.getInviteeUserId())
                        .groupId(groupId)
                        .groupName(groupName)
                        .inviterName(ownerName)
                        .invitationId(invitation.getId())
                        .build());
            } catch (Exception e) {
                System.err.println("Failed to send WebSocket notification on group creation: " + e.getMessage());
            }
        }
    }

    private TokenValidateResponse validateTokenOrThrow(String token) {
        TokenValidateResponse validation = userClient.validateToken(token);
        if (validation == null || !validation.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        return validation;
    }

    private String resolveUserName(Long userId, String fallbackName) {
        try {
            Map<String, Object> userData = userClient.getUserById(userId);
            if (userData == null) {
                return fallbackName;
            }
            return getStringValue(userData, fallbackName, "fullName", "full_name");
        } catch (Exception e) {
            return fallbackName;
        }
    }

    private String getStringValue(Map<String, Object> data, String fallback, String... keys) {
        for (String key : keys) {
            Object value = data.get(key);
            if (value instanceof String stringValue && StringUtils.hasText(stringValue)) {
                return stringValue;
            }
        }
        return fallback;
    }

    private void trySyncGroupParticipants(Long groupId, String actionName) {
        try {
            chatClient.syncGroupParticipants(groupId);
        } catch (Exception e) {
            System.err.println("Failed to sync group participants on " + actionName + ": " + e.getMessage());
        }
    }

    private String requireName(String value) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name must not be blank");
        }
        return normalizedValue;
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private GroupVisibility resolveStudyVisibility(String value) {
        if (!StringUtils.hasText(value)) {
            return GroupVisibility.PUBLIC;
        }

        try {
            GroupVisibility visibility = GroupVisibility.valueOf(value.trim().toUpperCase());
            if (visibility == GroupVisibility.COMMUNITY) {
                throw new IllegalArgumentException("COMMUNITY is not supported for study groups");
            }
            return visibility;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Visibility must be PUBLIC or PRIVATE for study groups");
        }
    }

    @Override
    public List<CommonGroupResponse> getCommonGroups(Long userId, Long otherUserId) {
        List<StudyGroup> commonGroups = groupMemberRepository.findCommonGroups(
                userId,
                otherUserId,
                GroupMemberStatus.ACTIVE,
                GroupStatus.ACTIVE);
        return commonGroups.stream()
                .map(group -> CommonGroupResponse.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .avatarUrl(group.getAvatarUrl())
                        .build())
                .toList();
    }

    @Override
    public boolean existsById(Long groupId) {
        if (groupId == null) return false;
        return studyGroupRepository.findById(groupId)
                .map(group -> group.getStatus() != GroupStatus.DELETED)
                .orElse(false);
    }
}
