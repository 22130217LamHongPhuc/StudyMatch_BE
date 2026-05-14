package com.group_service.service;

import com.group_service.dto.*;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.entity.enums.GroupVisibility;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudyGroupService {

    StudyGroupResponse createStudyGroup(CreateStudyGroupRequest request);

    StudyGroupResponse createCommunityGroup(CreateStudyGroupRequest request);

    StudyGroupDetailResponse getGroupById(Long groupId);

    List<StudyGroupDetailResponse> getGroupsByUserId(Long userId);

    Page<AdminGroupResponse> getGroupsForAdmin(GroupFilterRequest filter,int page, int limit);
    Page<AdminGroupResponse> getGroupsByKeywordForAdmin(String keyword, int page, int limit);

    GroupStats getStatsForGroups();


}

