package com.group_service.controller;

import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.StudyGroupResponse;
import com.group_service.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping
    public ResponseEntity<StudyGroupResponse> createGroup(@Valid @RequestBody CreateStudyGroupRequest request) {
        StudyGroupResponse response = studyGroupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

