package com.group_service.service;

import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.StudyGroupDetailResponse;
import com.group_service.dto.StudyGroupResponse;

import java.util.List;

public interface StudyGroupService {

    StudyGroupResponse createGroup(CreateStudyGroupRequest request);

    StudyGroupDetailResponse getGroupById(Long groupId);

    List<StudyGroupDetailResponse> getGroupsByUserId(Long userId);
}

