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
            Long currentUserId,
            int page,
            int limit
    );

    JoinGroupResponse joinGroup(Long groupId, JoinGroupRequest request);

    com.group_service.dto.GroupInvitationResponse sendInvitation(Long groupId, Long inviteeUserId, String token);

    List<com.group_service.dto.GroupInvitationResponse> getPendingInvitations(String token);

    List<com.group_service.dto.GroupInvitationResponse> getGroupInvitations(Long groupId, String token);

    void acceptInvitation(Long invitationId, String token);

    void rejectInvitation(Long invitationId, String token);

    UserGroupStatsResponse getCurrentUserGroupStats(Long userId);

}
