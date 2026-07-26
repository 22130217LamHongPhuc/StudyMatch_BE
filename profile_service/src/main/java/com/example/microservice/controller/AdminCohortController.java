package com.example.microservice.controller;

import com.example.microservice.dto.request.CohortRequest;
import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.entity.Cohort;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.services.AdminAcademicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cohorts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminCohortController {

    private final AdminAcademicService adminAcademicService;

    @GetMapping
    public ApiResponse<List<Cohort>> getAllCohorts() {
        List<Cohort> result = adminAcademicService.getAllCohorts();
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy danh sách khóa học thành công", result);
    }

    @PostMapping
    public ApiResponse<Cohort> createCohort(@RequestBody @Valid CohortRequest request) {
        Cohort result = adminAcademicService.createCohort(request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Tạo khóa học mới thành công", result);
    }

    @PutMapping("/{cohortId}")
    public ApiResponse<Cohort> updateCohort(
            @PathVariable Long cohortId,
            @RequestBody @Valid CohortRequest request
    ) {
        Cohort result = adminAcademicService.updateCohort(cohortId, request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Cập nhật thông tin khóa học thành công", result);
    }

    @DeleteMapping("/{cohortId}")
    public ApiResponse<Void> deleteCohort(@PathVariable Long cohortId) {
        adminAcademicService.deleteCohort(cohortId);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Xóa khóa học thành công", null);
    }
}
