package com.group_service.controller;

import com.group_service.dto.ApiResponse;
import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.JoinGroupRequest;
import com.group_service.dto.JoinGroupResponse;
import com.group_service.dto.StudyGroupDetailResponse;
import com.group_service.dto.StudyGroupResponse;
import com.group_service.entity.enums.GroupType;
import com.group_service.enums.StatusCode;
import com.group_service.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
//    private final ProfileClient profileClient;
//
//    @GetMapping("/test")
//    public Long testProfileClient() {
//        return profileClient.getActiveTerm();
//    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudyGroupResponse>> createGroup(@Valid @RequestBody CreateStudyGroupRequest request) {
//        Long termId = profileClient.getActiveTerm();
        request.setTermId(8L);
        StudyGroupResponse response = studyGroupService.createStudyGroup(request);
        return ResponseEntity.ok(new ApiResponse<>(
               true,
               StatusCode.SUCCESS,
               "create group successfully",
                response
        ));
    }


    @PostMapping("/community")
    public ResponseEntity<ApiResponse<StudyGroupResponse>> createCommunityGroup(@Valid @RequestBody CreateStudyGroupRequest request) {
//        Long termId = profileClient.getActiveTerm();
        request.setTermId(8L);
        StudyGroupResponse response = studyGroupService.createCommunityGroup(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "create  community group successfully",
                response
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<StudyGroupDetailResponse>>> getGroupsByUserId(@PathVariable Long userId) {
        List<StudyGroupDetailResponse> response = studyGroupService.getGroupsByUserId(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "create group successfully",
                response
        ));
    }

    @GetMapping("/browse")
    public ResponseEntity<ApiResponse<Page<StudyGroupResponse>>> browseGroups(
            @RequestParam(required = false) GroupType type,
            @RequestParam(required = false) Long subject,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page<StudyGroupResponse> response = studyGroupService.getGroupsByTypeAndSubject(
                type,
                subject,
                page,
                limit
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get groups successfully",
                response
        ));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<JoinGroupResponse>> joinGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody JoinGroupRequest request
    ) {
        JoinGroupResponse response = studyGroupService.joinGroup(groupId, request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Join group successfully",
                response
        ));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<StudyGroupDetailResponse> getGroup(@PathVariable Long groupId) {
        StudyGroupDetailResponse response = studyGroupService.getGroupById(groupId);
        return ResponseEntity.ok(response);
    }
}

