package com.group_service.controller;

import com.group_service.clients.ProfileClient;
import com.group_service.dto.ApiResponse;
import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.StudyGroupDetailResponse;
import com.group_service.dto.StudyGroupResponse;
import com.group_service.enums.StatusCode;
import com.group_service.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        StudyGroupResponse response = studyGroupService.createGroup(request);
        return ResponseEntity.ok(new ApiResponse<>(
               true,
               StatusCode.SUCCESS,
               "create group successfully",
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

    @GetMapping("/{groupId}")
    public ResponseEntity<StudyGroupDetailResponse> getGroup(@PathVariable Long groupId) {
        StudyGroupDetailResponse response = studyGroupService.getGroupById(groupId);
        return ResponseEntity.ok(response);
    }
}

