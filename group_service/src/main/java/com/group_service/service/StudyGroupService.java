package com.group_service.service;

import com.group_service.dto.*;
import com.group_service.dto.projection.GroupStats;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudyGroupService {

    StudyGroupResponse createStudyGroup(CreateStudyGroupRequest request, org.springframework.web.multipart.MultipartFile avatar);

    StudyGroupResponse createCommunityGroup(CreateStudyGroupRequest request, org.springframework.web.multipart.MultipartFile avatar);

    StudyGroupDetailResponse getGroupById(Long groupId);

    List<StudyGroupDetailResponse> getGroupsByUserId(Long userId);

    Page<AdminGroupResponse> getGroupsForAdmin(GroupFilterRequest filter,int page, int limit);
    Page<AdminGroupResponse> getGroupsByKeywordForAdmin(String keyword, int page, int limit);

    GroupStats getStatsForGroups();

    AdminGroupDetailResponse getGroupDetailForAdmin(Long groupId);

    AdminGroupDetailResponse updateGroupStatusForAdmin(Long groupId, UpdateGroupStatusRequest request);

    Page<StudyGroupResponse> getGroupsByTypeAndSubject(
            com.group_service.entity.enums.GroupType groupType,
            Long mainSubjectId,
            String keyword,
            Long currentUserId,
            int page,
            int limit
    );

    GroupInvitationResponse joinGroup(Long groupId, JoinGroupRequest request);

    com.group_service.dto.GroupInvitationResponse sendInvitation(Long groupId, Long inviteeUserId, String message, String token);

    List<com.group_service.dto.GroupInvitationResponse> getPendingInvitations(String token);

    List<com.group_service.dto.GroupInvitationResponse> getSentPendingJoinRequests(String token);

    List<com.group_service.dto.GroupInvitationResponse> getReceivedPendingJoinRequests(String token);

    List<com.group_service.dto.GroupInvitationResponse> getGroupInvitations(Long groupId, String token);

    void acceptInvitation(Long invitationId, String token);

    void rejectInvitation(Long invitationId, String token);

    UserGroupStatsResponse getCurrentUserGroupStats(Long userId);

    List<CommonGroupResponse> getCommonGroups(Long userId, Long otherUserId);

    boolean existsById(Long groupId);

    void removeGroupMemberForAdmin(Long groupId, Long userId);

    void changeGroupOwnerForAdmin(Long groupId, Long newOwnerUserId);

    void updateStudyGroupForAdmin(Long groupId, CreateStudyGroupRequest.UpdateStudyGroupRequest request, org.springframework.web.multipart.MultipartFile avatar);

    void updateStudyGroup(Long groupId, CreateStudyGroupRequest.UpdateStudyGroupRequest request, org.springframework.web.multipart.MultipartFile avatar, String token);

    void deleteStudyGroup(Long groupId, String token);
}

