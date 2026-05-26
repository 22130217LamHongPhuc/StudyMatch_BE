package com.group_service.controller;

import com.group_service.dto.ApiResponse;
import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.dto.StudySessionResponse;
import com.group_service.enums.StatusCode;
import com.group_service.service.StudySessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/groups/{groupId}/sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService studySessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudySessionResponse>> createSession(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateStudySessionRequest request
    ) {
        StudySessionResponse response = studySessionService.createSession(groupId, request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "create study session successfully",
                response
        ));
    }
}

