package com.example.microservice.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CohortStudyPlanResponse {
    private Long cohortId;
    private String cohortCode;
    private Integer startAcademicYear;
    private Integer totalStudyYears;

    private Long curriculumId;
    private String curriculumCode;
    private String curriculumName;

    private Long termId;
    private Integer academicYearStart;
    private Integer academicYearEnd;
    private Integer semesterNo;
    private String termFullName;

    private Integer studyYearNo;

    private List<SubjectItem> subjects;

    public static class SubjectItem {
        private Long subjectId;
        private String subjectCode;
        private String subjectName;
        private Boolean required;
        private Integer recommendedOrder;

        public SubjectItem() {
        }

        public SubjectItem(Long subjectId, String subjectCode, String subjectName, Boolean required, Integer recommendedOrder) {
            this.subjectId = subjectId;
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;
            this.required = required;
            this.recommendedOrder = recommendedOrder;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public void setSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public Integer getRecommendedOrder() {
            return recommendedOrder;
        }

        public void setRecommendedOrder(Integer recommendedOrder) {
            this.recommendedOrder = recommendedOrder;
        }
    }

}