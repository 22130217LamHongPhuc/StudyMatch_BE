package com.example.microservice.dto.response;


import java.util.List;

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

    public Long getCohortId() {
        return cohortId;
    }

    public void setCohortId(Long cohortId) {
        this.cohortId = cohortId;
    }

    public String getCohortCode() {
        return cohortCode;
    }

    public void setCohortCode(String cohortCode) {
        this.cohortCode = cohortCode;
    }

    public Integer getStartAcademicYear() {
        return startAcademicYear;
    }

    public void setStartAcademicYear(Integer startAcademicYear) {
        this.startAcademicYear = startAcademicYear;
    }

    public Integer getTotalStudyYears() {
        return totalStudyYears;
    }

    public void setTotalStudyYears(Integer totalStudyYears) {
        this.totalStudyYears = totalStudyYears;
    }

    public Long getCurriculumId() {
        return curriculumId;
    }

    public void setCurriculumId(Long curriculumId) {
        this.curriculumId = curriculumId;
    }

    public String getCurriculumCode() {
        return curriculumCode;
    }

    public void setCurriculumCode(String curriculumCode) {
        this.curriculumCode = curriculumCode;
    }

    public String getCurriculumName() {
        return curriculumName;
    }

    public void setCurriculumName(String curriculumName) {
        this.curriculumName = curriculumName;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Integer getAcademicYearStart() {
        return academicYearStart;
    }

    public void setAcademicYearStart(Integer academicYearStart) {
        this.academicYearStart = academicYearStart;
    }

    public Integer getAcademicYearEnd() {
        return academicYearEnd;
    }

    public void setAcademicYearEnd(Integer academicYearEnd) {
        this.academicYearEnd = academicYearEnd;
    }

    public Integer getSemesterNo() {
        return semesterNo;
    }

    public void setSemesterNo(Integer semesterNo) {
        this.semesterNo = semesterNo;
    }

    public String getTermFullName() {
        return termFullName;
    }

    public void setTermFullName(String termFullName) {
        this.termFullName = termFullName;
    }

    public Integer getStudyYearNo() {
        return studyYearNo;
    }

    public void setStudyYearNo(Integer studyYearNo) {
        this.studyYearNo = studyYearNo;
    }

    public List<SubjectItem> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectItem> subjects) {
        this.subjects = subjects;
    }
}