package com.example.microservice.controller;

import com.example.microservice.dto.request.SubjectRequest;
import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.entity.Subject;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.services.AdminAcademicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subjects")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminSubjectController {

    private final AdminAcademicService adminAcademicService;

    @GetMapping
    public ApiResponse<Page<Subject>> searchSubjects(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Subject> result = adminAcademicService.searchSubjects(search, pageable);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy danh sách môn học thành công", result);
    }

    @PostMapping
    public ApiResponse<Subject> createSubject(@RequestBody @Valid SubjectRequest request) {
        Subject result = adminAcademicService.createSubject(request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Thêm môn học thành công", result);
    }

    @PutMapping("/{subjectId}")
    public ApiResponse<Subject> updateSubject(
            @PathVariable Long subjectId,
            @RequestBody @Valid SubjectRequest request
    ) {
        Subject result = adminAcademicService.updateSubject(subjectId, request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Cập nhật môn học thành công", result);
    }

    @DeleteMapping("/{subjectId}")
    public ApiResponse<Void> deleteSubject(@PathVariable Long subjectId) {
        adminAcademicService.deleteSubject(subjectId);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Xóa môn học thành công", null);
    }

    @PostMapping("/import")
    public ApiResponse<List<Subject>> importSubjects(@RequestBody List<SubjectRequest> requests) {
        List<Subject> result = adminAcademicService.importSubjects(requests);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Import danh sách môn học thành công", result);
    }
}
