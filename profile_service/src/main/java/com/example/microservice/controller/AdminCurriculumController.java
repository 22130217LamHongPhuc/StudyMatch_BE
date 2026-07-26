package com.example.microservice.controller;

import com.example.microservice.dto.request.CurriculumRequest;
import com.example.microservice.dto.request.CurriculumSubjectRequest;
import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.entity.Curriculum;
import com.example.microservice.entity.CurriculumTermSubject;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.services.AdminAcademicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/curriculums")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminCurriculumController {

    private final AdminAcademicService adminAcademicService;

    @GetMapping
    public ApiResponse<List<Curriculum>> getAllCurriculums() {
        List<Curriculum> result = adminAcademicService.getAllCurriculums();
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy danh sách chương trình đào tạo thành công", result);
    }

    @PostMapping
    public ApiResponse<Curriculum> createCurriculum(@RequestBody @Valid CurriculumRequest request) {
        Curriculum result = adminAcademicService.createCurriculum(request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Tạo chương trình đào tạo thành công", result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Curriculum> updateCurriculum(
            @PathVariable Long id,
            @RequestBody @Valid CurriculumRequest request
    ) {
        Curriculum result = adminAcademicService.updateCurriculum(id, request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Cập nhật chương trình đào tạo thành công", result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCurriculum(@PathVariable Long id) {
        adminAcademicService.deleteCurriculum(id);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Xóa chương trình đào tạo thành công", null);
    }

    @GetMapping("/{id}/subjects")
    public ApiResponse<List<CurriculumTermSubject>> getCurriculumSubjects(@PathVariable Long id) {
        List<CurriculumTermSubject> result = adminAcademicService.getCurriculumSubjects(id);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy danh sách môn học của chương trình đào tạo thành công", result);
    }

    @PostMapping("/{id}/subjects")
    public ApiResponse<CurriculumTermSubject> addSubjectToCurriculum(
            @PathVariable Long id,
            @RequestBody @Valid CurriculumSubjectRequest request
    ) {
        CurriculumTermSubject result = adminAcademicService.addSubjectToCurriculum(id, request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Thêm môn học vào chương trình đào tạo thành công", result);
    }

    @DeleteMapping("/{curriculumId}/subjects/{subjectId}")
    public ApiResponse<Void> removeSubjectFromCurriculum(
            @PathVariable Long curriculumId,
            @PathVariable Long subjectId
    ) {
        adminAcademicService.removeSubjectFromCurriculum(curriculumId, subjectId);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Gỡ môn học khỏi chương trình đào tạo thành công", null);
    }
}
