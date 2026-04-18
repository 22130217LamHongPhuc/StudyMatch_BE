package com.example.microservice.services;


import com.example.microservice.dto.request.SubjectByCohortAndCurriculumRequest;
import com.example.microservice.dto.response.CohortStudyPlanResponse;
import com.example.microservice.entity.AcademicTerm;
import com.example.microservice.entity.Cohort;
import com.example.microservice.entity.CurriculumTermSubject;
import com.example.microservice.entity.Subject;
import com.example.microservice.repositories.AcademicTermRepository;
import com.example.microservice.repositories.CohortRepository;
import com.example.microservice.repositories.CurriculumTermSubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CohortStudyPlanService {

    private final CohortRepository cohortRepository;
    private final AcademicTermRepository academicTermRepository;
    private final CurriculumTermSubjectRepository curriculumTermSubjectRepository;

    public CohortStudyPlanService(
            CohortRepository cohortRepository,
            AcademicTermRepository academicTermRepository,
            CurriculumTermSubjectRepository curriculumTermSubjectRepository
    ) {
        this.cohortRepository = cohortRepository;
        this.academicTermRepository = academicTermRepository;
        this.curriculumTermSubjectRepository = curriculumTermSubjectRepository;
    }

    public CohortStudyPlanResponse getCurrentStudyPlanByCohortCode(String cohortCode) {
        Cohort cohort = cohortRepository.findByCohortCode(cohortCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa: " + cohortCode));

        AcademicTerm activeTerm = academicTermRepository.findFirstByStatus("active")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ đang active"));

        int studyYearNo = activeTerm.getAcademicYearStart() - cohort.getStartAcademicYear() + 1;
        int semesterNo = activeTerm.getSemesterNo() != null ? activeTerm.getSemesterNo() : 0;
        int totalStudyYears = cohort.getTotalStudyYears() != null ? cohort.getTotalStudyYears() : 4;

        if (studyYearNo < 1) {
            throw new RuntimeException(
                    "Khóa " + cohortCode + " không nằm trong phạm vi đào tạo ở học kỳ hiện tại"
            );
        }

        studyYearNo = Math.min(studyYearNo, totalStudyYears);


        List<CurriculumTermSubject> curriculumSubjects =
                curriculumTermSubjectRepository
                        .findByCurriculum_CurriculumIdAndStudyYearNoAndSemesterNoOrderByRecommendedOrderAsc(
                                cohort.getCurriculum().getCurriculumId(),
                                studyYearNo,
                                semesterNo
                        );

        CohortStudyPlanResponse response = new CohortStudyPlanResponse();
        response.setCohortId(cohort.getCohortId());
        response.setCohortCode(cohort.getCohortCode());
        response.setStartAcademicYear(cohort.getStartAcademicYear());
        response.setTotalStudyYears(totalStudyYears);

        response.setCurriculumId(cohort.getCurriculum().getCurriculumId());
        response.setCurriculumCode(cohort.getCurriculum().getCurriculumCode());
        response.setCurriculumName(cohort.getCurriculum().getCurriculumName());

        response.setTermId(activeTerm.getTermId());
        response.setAcademicYearStart(activeTerm.getAcademicYearStart());
        response.setAcademicYearEnd(activeTerm.getAcademicYearEnd());
        response.setSemesterNo(activeTerm.getSemesterNo() != null ? activeTerm.getSemesterNo().intValue() : null);
        response.setTermFullName(activeTerm.getFullName());

        response.setStudyYearNo(studyYearNo);

        List<CohortStudyPlanResponse.SubjectItem> subjectItems = curriculumSubjects.stream()
                .map(item -> {
                    Subject s = item.getSubject();
                    return new CohortStudyPlanResponse.SubjectItem(
                            s.getSubjectId(),
                            s.getSubjectCode(),
                            s.getSubjectName(),
                            item.getRequired(),
                            item.getRecommendedOrder()
                    );
                })
                .toList();

        response.setSubjects(subjectItems);

        return response;
    }

    public CohortStudyPlanResponse getSubjectByCohortCodeAndCurriculum(
            String cohortCode, SubjectByCohortAndCurriculumRequest request
    ) {
        Cohort cohort = cohortRepository.findByCohortCode(cohortCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa: " + cohortCode));


        int studyYearNo = request.getStudyYearNo();
        int semesterNo = request.getSemesterNo();
        int startYearTerm = request.getStartYearTerm();
        int endYearTerm = request.getEndYearTerm();

        int totalStudyYears = cohort.getTotalStudyYears() != null ? cohort.getTotalStudyYears() : 4;
        studyYearNo = Math.min(studyYearNo, totalStudyYears);


        List<CurriculumTermSubject> curriculumSubjects =
                curriculumTermSubjectRepository
                        .findByCurriculum_CurriculumIdAndStudyYearNoAndSemesterNoOrderByRecommendedOrderAsc(
                                cohort.getCurriculum().getCurriculumId(),
                                studyYearNo,
                                semesterNo
                        );
        AcademicTerm academicTerm = academicTermRepository.findByAcademicYearStartBetweenOrderByAcademicYearStartAscSemesterNoAsc(
                        (short) startYearTerm, (short) endYearTerm
                        ).stream().filter(term -> term.getSemesterNo() != null && term.getSemesterNo() == semesterNo).findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ phù hợp"));

        CohortStudyPlanResponse response = new CohortStudyPlanResponse();
        response.setCohortId(cohort.getCohortId());
        response.setCohortCode(cohort.getCohortCode());
        response.setStartAcademicYear(cohort.getStartAcademicYear());
        response.setTotalStudyYears(totalStudyYears);

        response.setCurriculumId(cohort.getCurriculum().getCurriculumId());
        response.setCurriculumCode(cohort.getCurriculum().getCurriculumCode());
        response.setCurriculumName(cohort.getCurriculum().getCurriculumName());

        response.setTermId(academicTerm.getTermId());
        response.setAcademicYearStart(academicTerm.getAcademicYearStart());
        response.setAcademicYearEnd(academicTerm.getAcademicYearEnd());
        response.setSemesterNo(academicTerm.getSemesterNo() != null ? academicTerm.getSemesterNo().intValue() : null);
        response.setTermFullName(academicTerm.getFullName());

        response.setStudyYearNo(studyYearNo);

        List<CohortStudyPlanResponse.SubjectItem> subjectItems = curriculumSubjects.stream()
                .map(item -> {
                    Subject s = item.getSubject();
                    return new CohortStudyPlanResponse.SubjectItem(
                            s.getSubjectId(),
                            s.getSubjectCode(),
                            s.getSubjectName(),
                            item.getRequired(),
                            item.getRecommendedOrder()
                    );
                })
                .toList();

        response.setSubjects(subjectItems);

        return response;
    }


}