package com.example.microservice.controller;

import com.example.microservice.dto.response.AcademicStatsResponse;
import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.entity.StudentProfile;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.services.AdminAcademicService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/profiles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminProfileController {

    private final AdminAcademicService adminAcademicService;

    @GetMapping
    public ApiResponse<Page<StudentProfile>> searchProfiles(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Long cohortId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentProfile> result = adminAcademicService.searchProfiles(search, cohortId, pageable);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy danh sách hồ sơ sinh viên thành công", result);
    }

    @GetMapping("/{profileId}")
    public ApiResponse<StudentProfile> getProfileDetail(@PathVariable Long profileId) {
        StudentProfile result = adminAcademicService.getProfileDetail(profileId);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy thông tin chi tiết hồ sơ sinh viên thành công", result);
    }

    @PutMapping("/{profileId}")
    public ApiResponse<StudentProfile> updateStudentProfile(
            @PathVariable Long profileId,
            @RequestBody UpdateProfileRequest request
    ) {
        StudentProfile result = adminAcademicService.updateStudentProfile(
                profileId,
                request.getCohortId(),
                request.getStudentCode(),
                request.getFullName(),
                request.getRegion(),
                request.getGender()
        );
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Cập nhật hồ sơ sinh viên thành công", result);
    }

    @GetMapping("/stats")
    public ApiResponse<AcademicStatsResponse> getAcademicStats() {
        AcademicStatsResponse result = adminAcademicService.getAcademicStats();
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy số liệu thống kê học tập thành công", result);
    }

    @Setter
    @Getter
    public static class UpdateProfileRequest {
        private Long cohortId;
        private String studentCode;
        private String fullName;
        private String region;
        private String gender;
    }
}
