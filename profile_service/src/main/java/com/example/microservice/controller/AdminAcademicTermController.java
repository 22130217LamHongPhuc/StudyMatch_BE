package com.example.microservice.controller;

import com.example.microservice.dto.request.AcademicTermRequest;
import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.entity.AcademicTerm;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.services.AdminAcademicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/academic-terms")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminAcademicTermController {

    private final AdminAcademicService adminAcademicService;

    @GetMapping
    public ApiResponse<List<AcademicTerm>> getAllAcademicTerms() {
        List<AcademicTerm> result = adminAcademicService.getAllAcademicTerms();
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Lấy danh sách học kỳ thành công", result);
    }

    @PostMapping
    public ApiResponse<AcademicTerm> createAcademicTerm(@RequestBody @Valid AcademicTermRequest request) {
        AcademicTerm result = adminAcademicService.createAcademicTerm(request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Thêm học kỳ mới thành công", result);
    }

    @PutMapping("/{termId}")
    public ApiResponse<AcademicTerm> updateAcademicTerm(
            @PathVariable Long termId,
            @RequestBody @Valid AcademicTermRequest request
    ) {
        AcademicTerm result = adminAcademicService.updateAcademicTerm(termId, request);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Cập nhật học kỳ thành công", result);
    }

    @PutMapping("/{termId}/active")
    public ApiResponse<Void> activateAcademicTerm(@PathVariable Long termId) {
        adminAcademicService.activateAcademicTerm(termId);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "Kích hoạt học kỳ hiện tại thành công", null);
    }
}
