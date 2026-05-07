package com.group_service.service;

import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.StudyGroupResponse;

public interface StudyGroupService {

    StudyGroupResponse createGroup(CreateStudyGroupRequest request);
}

