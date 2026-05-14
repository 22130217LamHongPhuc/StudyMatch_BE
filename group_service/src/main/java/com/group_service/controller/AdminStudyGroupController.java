package com.group_service.controller;

import com.group_service.dto.AdminGroupResponse;
import com.group_service.dto.ApiResponse;
import com.group_service.dto.GroupFilterRequest;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.enums.StatusCode;
import com.group_service.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/admin/groups")
@RequiredArgsConstructor
public class AdminStudyGroupController {

    private final StudyGroupService studyGroupService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminGroupResponse>>> getGroupsForAdmin(
            @RequestParam(required = false) GroupType type,
            @RequestParam(required = false) GroupStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        GroupFilterRequest filter = new GroupFilterRequest();
        filter.setType(type);
        filter.setStatus(status);

        Page<AdminGroupResponse> response =
                studyGroupService.getGroupsForAdmin(filter, page, limit);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get groups successfully",
                response
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<GroupStats>> getStatsForGroups() {
        GroupStats response = studyGroupService.getStatsForGroups();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get group stats successfully",
                response
        ));
    }
    @GetMapping("search")
    public ResponseEntity<ApiResponse<Page<AdminGroupResponse>>>    searchGroupsForAdmin(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {


        Page<AdminGroupResponse> response =
                studyGroupService.getGroupsByKeywordForAdmin(keyword, page, limit);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Search groups successfully",
                response
        ));
    }


}