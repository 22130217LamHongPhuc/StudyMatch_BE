package com.example.microservice.controller;


import com.example.microservice.dto.response.CohortStudyPlanResponse;
import com.example.microservice.entity.Cohort;
import com.example.microservice.entity.Curriculum;
import com.example.microservice.repositories.CohortRepository;
import com.example.microservice.repositories.CurriculumTermSubjectRepository;
import com.example.microservice.repositories.StudyYearSemesterProjection;
import com.example.microservice.services.CohortStudyPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/cohorts")
public class CohortStudyPlanController {

    private final CohortRepository cohortRepository;
    private final CurriculumTermSubjectRepository curriculumTermSubjectRepository;
    private final CohortStudyPlanService cohortStudyPlanService;

    public CohortStudyPlanController(
            CohortRepository cohortRepository,
            CurriculumTermSubjectRepository curriculumTermSubjectRepository,
            CohortStudyPlanService cohortStudyPlanService
    ) {
        this.cohortRepository = cohortRepository;
        this.curriculumTermSubjectRepository = curriculumTermSubjectRepository;
        this.cohortStudyPlanService = cohortStudyPlanService;
    }


    @GetMapping()
    public ResponseEntity<List<Cohort>> getAllCohorts() {
        List<Cohort> cohorts = cohortRepository.findAll();
        return ResponseEntity.ok(cohorts);
    }


    @GetMapping("/{cohortCode}/study-plan/current")
    public ResponseEntity<CohortStudyPlanResponse> getCurrentStudyPlan(
            @PathVariable String cohortCode
    ) {
        System.out.println("Received request for current study plan of cohort: " + cohortCode);
        CohortStudyPlanResponse response =
                cohortStudyPlanService.getCurrentStudyPlanByCohortCode(cohortCode);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{cohortCode}/study-plan-options")
    public ResponseEntity<?> getStudyPlanOptionsByCohort(@PathVariable String cohortCode) {
        Optional<Cohort> cohortOpt = cohortRepository.findByCohortCode(cohortCode);

        if (cohortOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Không tìm thấy khóa: " + cohortCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        Cohort cohort = cohortOpt.get();
        Curriculum curriculum = cohort.getCurriculum();

        if (curriculum == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Khóa " + cohortCode + " chưa được gán chương trình đào tạo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        List<StudyYearSemesterProjection> rows =
                curriculumTermSubjectRepository.findStudyYearsAndSemestersByCurriculumId(curriculum.getCurriculumId());

        Map<Integer, List<Integer>> grouped = new LinkedHashMap<>();
        for (StudyYearSemesterProjection row : rows) {
            grouped.computeIfAbsent(row.getStudyYearNo(), k -> new ArrayList<>());
            if (!grouped.get(row.getStudyYearNo()).contains(row.getSemesterNo())) {
                grouped.get(row.getStudyYearNo()).add(row.getSemesterNo());
            }
        }

        List<Map<String, Object>> studyYears = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : grouped.entrySet()) {
            Integer studyYearNo = entry.getKey();
            List<Integer> semesterNos = entry.getValue();

            List<Map<String, Object>> semesters = new ArrayList<>();
            semesterNos.sort(Integer::compareTo);

            for (Integer semesterNo : semesterNos) {
                Map<String, Object> semesterMap = new LinkedHashMap<>();
                semesterMap.put("semesterNo", semesterNo);
                semesterMap.put("displayName", "Học kỳ " + semesterNo);
                semesters.add(semesterMap);
            }

            Map<String, Object> studyYearMap = new LinkedHashMap<>();
            studyYearMap.put("studyYearNo", studyYearNo);
            studyYearMap.put("displayName", "Năm " + studyYearNo);

            Integer startYear = cohort.getStartAcademicYear();
            if (startYear != null) {
                int academicYearStart = startYear + (studyYearNo - 1);
                int academicYearEnd = academicYearStart + 1;
                studyYearMap.put("academicYearLabel", academicYearStart + "-" + academicYearEnd);
            } else {
                studyYearMap.put("academicYearLabel", null);
            }

            studyYearMap.put("semesters", semesters);
            studyYears.add(studyYearMap);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("cohortCode", cohort.getCohortCode());
        response.put("startYear", cohort.getStartAcademicYear());
        response.put("curriculumId", curriculum.getCurriculumId());
        response.put("curriculumCode", curriculum.getCurriculumCode());
        response.put("curriculumName", curriculum.getCurriculumName());
        response.put("studyYears", studyYears);

        return ResponseEntity.ok(response);
    }



}