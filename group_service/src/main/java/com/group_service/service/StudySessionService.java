package com.group_service.service;

import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.dto.StudySessionResponse;

public interface StudySessionService {

    StudySessionResponse createSession(Long groupId, CreateStudySessionRequest request);
}

