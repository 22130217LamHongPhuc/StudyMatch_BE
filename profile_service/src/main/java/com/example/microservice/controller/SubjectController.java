package com.example.microservice.controller;

import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.dto.response.SubjectInfoResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.repositories.CurriculumTermSubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final CurriculumTermSubjectRepository curriculumTermSubjectRepository;

    public SubjectController(CurriculumTermSubjectRepository curriculumTermSubjectRepository) {
        this.curriculumTermSubjectRepository = curriculumTermSubjectRepository;
    }

    @GetMapping("/by-curriculum/{curriculumId}")
    public ResponseEntity<ApiResponse<List<SubjectInfoResponse>>> getSubjectsByCurriculumId(
            @PathVariable Long curriculumId
    ) {
        List<SubjectInfoResponse> subjects =
                curriculumTermSubjectRepository.findDistinctSubjectsByCurriculumId(curriculumId);

        return ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS,"Subjects retrieved successfully", subjects));
    }
}
