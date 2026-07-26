package com.example.microservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class AcademicStatsResponse {
    private Long totalStudentsCount;
    private Map<String, Long> studentsPerCohort;
    private List<SubjectEnrollmentStat> topEnrolledSubjects;
    private Map<String, Long> studentsByRegion;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class SubjectEnrollmentStat {
        private String subjectName;
        private Long enrollmentCount;
    }
}
