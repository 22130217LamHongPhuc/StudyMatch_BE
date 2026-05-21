package com.example.microservice.controller;

import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.dto.response.SubjectInfoResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.repositories.CurriculumTermSubjectRepository;
import com.example.microservice.repositories.SubjectRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/subjects")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE,makeFinal = true)
@AllArgsConstructor

public class SubjectController {

    CurriculumTermSubjectRepository curriculumTermSubjectRepository;

     SubjectRepository subjectRepository;



    @GetMapping
    public ApiResponse<List<SubjectInfoResponse>> getAllSubjects() {
        List<SubjectInfoResponse> subjects = subjectRepository.getAllSubjects();
        return new ApiResponse<>(true, StatusCode.SUCCESS,"Get All subjects  successfully", subjects);
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
