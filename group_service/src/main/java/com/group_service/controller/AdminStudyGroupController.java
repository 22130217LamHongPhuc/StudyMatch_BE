package com.group_service.controller;

import com.group_service.annotation.AuditLog;
import com.group_service.dto.AdminGroupResponse;
import com.group_service.dto.AdminGroupDetailResponse;
import com.group_service.dto.ApiResponse;
import com.group_service.dto.GroupFilterRequest;
import com.group_service.dto.UpdateGroupStatusRequest;
import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.enums.StatusCode;
import com.group_service.service.StudyGroupService;
import jakarta.validation.Valid;
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
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        GroupFilterRequest filter = new GroupFilterRequest();
        filter.setType(type);
        filter.setStatus(status);
        filter.setKeyword(keyword);

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

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<AdminGroupDetailResponse>> getGroupDetailForAdmin(
            @PathVariable Long groupId
    ) {
        AdminGroupDetailResponse response = studyGroupService.getGroupDetailForAdmin(groupId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get group detail successfully",
                response
        ));
    }

    @PatchMapping("/{groupId}/status")
    @AuditLog(action = "UPDATE_GROUP_STATUS", targetType = "STUDY_GROUP", targetId = "#groupId", details = "'Cập nhật trạng thái nhóm học tập: ' + #request.status")
    public ResponseEntity<ApiResponse<AdminGroupDetailResponse>> updateGroupStatusForAdmin(
            @PathVariable Long groupId,
            @Valid @RequestBody UpdateGroupStatusRequest request
    ) {
        AdminGroupDetailResponse response = studyGroupService.updateGroupStatusForAdmin(groupId, request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Update group status successfully",
                response
        ));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @AuditLog(action = "REMOVE_GROUP_MEMBER", targetType = "STUDY_GROUP", targetId = "#groupId", details = "'Xóa thành viên ID: ' + #userId + ' khỏi nhóm'")
    public ResponseEntity<ApiResponse<Void>> removeGroupMemberForAdmin(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        studyGroupService.removeGroupMemberForAdmin(groupId, userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Remove group member successfully",
                null
        ));
    }

    @PutMapping("/{groupId}/owner/{newOwnerUserId}")
    @AuditLog(action = "CHANGE_GROUP_OWNER", targetType = "STUDY_GROUP", targetId = "#groupId", details = "'Chuyển quyền sở hữu nhóm cho thành viên ID: ' + #newOwnerUserId")
    public ResponseEntity<ApiResponse<Void>> changeGroupOwnerForAdmin(
            @PathVariable Long groupId,
            @PathVariable Long newOwnerUserId
    ) {
        studyGroupService.changeGroupOwnerForAdmin(groupId, newOwnerUserId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Change group owner successfully",
                null
        ));
    }

    @PutMapping(value = "/{groupId}", consumes = {"multipart/form-data"})
    @AuditLog(action = "UPDATE_GROUP_DETAILS", targetType = "STUDY_GROUP", targetId = "#groupId", details = "'Cập nhật thông tin chi tiết nhóm học: ' + #request.name")
    public ResponseEntity<ApiResponse<Void>> updateGroupDetailsForAdmin(
            @PathVariable Long groupId,
            @RequestPart("request") @Valid CreateStudyGroupRequest.UpdateStudyGroupRequest request,
            @RequestPart(value = "avatar", required = false) org.springframework.web.multipart.MultipartFile avatar
    ) {
        studyGroupService.updateStudyGroupForAdmin(groupId, request, avatar);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Update group details successfully",
                null
        ));
    }
}