package com.example.microservice.services;

import com.example.microservice.dto.request.*;
import com.example.microservice.dto.response.AcademicStatsResponse;
import com.example.microservice.entity.*;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAcademicService {

    private final SubjectRepository subjectRepository;
    private final CurriculumRepository curriculumRepository;
    private final CurriculumTermSubjectRepository curriculumTermSubjectRepository;
    private final CohortRepository cohortRepository;
    private final AcademicTermRepository academicTermRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSubjectEnrollmentRepository studentSubjectEnrollmentRepository;

    // ==========================================
    // SUBJECTS MANAGEMENT
    // ==========================================

    public Page<Subject> searchSubjects(String search, Pageable pageable) {
        return subjectRepository.searchSubjects(search, pageable);
    }

    public Subject createSubject(SubjectRequest request) {
        Subject subject = new Subject();
        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long subjectId, SubjectRequest request) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new AppException("Không tìm thấy môn học", StatusCode.NOT_FOUND));
        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new AppException("Không tìm thấy môn học", StatusCode.NOT_FOUND);
        }
        if (curriculumTermSubjectRepository.existsBySubject_SubjectId(subjectId)) {
            throw new AppException("Môn học đã tồn tại trong chương trình đào tạo, không thể xóa.",
                    StatusCode.EMAIL_ALREADY_IN_USE); // USING EMAIL_ALREADY_IN_USE as a generic validation error code
        }
        if (studentSubjectEnrollmentRepository.existsBySubject_SubjectId(subjectId)) {
            throw new AppException("Đã có sinh viên đăng ký học môn này, không thể xóa.",
                    StatusCode.EMAIL_ALREADY_IN_USE);
        }
        subjectRepository.deleteById(subjectId);
    }

    @Transactional
    public List<Subject> importSubjects(List<SubjectRequest> requests) {
        List<Subject> savedSubjects = new ArrayList<>();
        for (SubjectRequest req : requests) {
            Optional<Subject> existingOpt = subjectRepository.getAllSubjects().stream()
                    .map(subResponse -> {
                        // find full subject object
                        return subjectRepository.findById(subResponse.getSubjectId()).orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .filter(s -> s.getSubjectCode().equalsIgnoreCase(req.getSubjectCode()))
                    .findFirst();

            if (existingOpt.isPresent()) {
                Subject existing = existingOpt.get();
                existing.setSubjectName(req.getSubjectName());
                savedSubjects.add(subjectRepository.save(existing));
            } else {
                Subject subject = new Subject();
                subject.setSubjectCode(req.getSubjectCode());
                subject.setSubjectName(req.getSubjectName());
                savedSubjects.add(subjectRepository.save(subject));
            }
        }
        return savedSubjects;
    }

    // ==========================================
    // CURRICULUMS MANAGEMENT
    // ==========================================

    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    public Curriculum createCurriculum(CurriculumRequest request) {
        Curriculum curriculum = new Curriculum();
        curriculum.setCurriculumCode(request.getCurriculumCode());
        curriculum.setCurriculumName(request.getCurriculumName());
        return curriculumRepository.save(curriculum);
    }

    public Curriculum updateCurriculum(Long id, CurriculumRequest request) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy chương trình đào tạo", StatusCode.NOT_FOUND));
        curriculum.setCurriculumCode(request.getCurriculumCode());
        curriculum.setCurriculumName(request.getCurriculumName());
        return curriculumRepository.save(curriculum);
    }

    public void deleteCurriculum(Long id) {
        if (!curriculumRepository.existsById(id)) {
            throw new AppException("Không tìm thấy chương trình đào tạo", StatusCode.NOT_FOUND);
        }
        if (curriculumTermSubjectRepository.existsByCurriculum_CurriculumId(id)) {
            throw new AppException("Chương trình đào tạo đã được cấu hình môn học, không thể xóa.",
                    StatusCode.EMAIL_ALREADY_IN_USE);
        }
        curriculumRepository.deleteById(id);
    }

    public List<CurriculumTermSubject> getCurriculumSubjects(Long curriculumId) {
        return curriculumTermSubjectRepository.findByCurriculum_CurriculumId(curriculumId);
    }

    @Transactional
    public CurriculumTermSubject addSubjectToCurriculum(Long curriculumId, CurriculumSubjectRequest request) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new AppException("Không tìm thấy chương trình đào tạo", StatusCode.NOT_FOUND));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException("Không tìm thấy môn học", StatusCode.NOT_FOUND));

        List<CurriculumTermSubject> existing = curriculumTermSubjectRepository
                .findByCurriculum_CurriculumId(curriculumId);
        boolean duplicate = existing.stream()
                .anyMatch(cts -> cts.getStudyYearNo().intValue() == request.getStudyYearNo() &&
                        cts.getSemesterNo().intValue() == request.getSemesterNo() &&
                        cts.getSubject().getSubjectId().equals(request.getSubjectId()));
        if (duplicate) {
            throw new AppException("Môn học này đã được thêm vào học kỳ của chương trình đào tạo",
                    StatusCode.EMAIL_ALREADY_IN_USE);
        }

        CurriculumTermSubject mapping = new CurriculumTermSubject();
        mapping.setCurriculum(curriculum);
        mapping.setSubject(subject);
        mapping.setStudyYearNo(request.getStudyYearNo().byteValue());
        mapping.setSemesterNo(request.getSemesterNo().byteValue());
        mapping.setRequired(request.getIsRequired() != null ? request.getIsRequired() : Boolean.TRUE);
        mapping.setRecommendedOrder(request.getRecommendedOrder());

        return curriculumTermSubjectRepository.save(mapping);
    }

    @Transactional
    public void removeSubjectFromCurriculum(Long curriculumId, Long subjectId) {
        curriculumTermSubjectRepository.deleteByCurriculum_CurriculumIdAndSubject_SubjectId(curriculumId, subjectId);
    }

    // ==========================================
    // COHORTS MANAGEMENT
    // ==========================================

    public List<Cohort> getAllCohorts() {
        return cohortRepository.findAll();
    }

    public Cohort createCohort(CohortRequest request) {
        Curriculum curriculum = curriculumRepository.findById(request.getCurriculumId())
                .orElseThrow(() -> new AppException("Không tìm thấy chương trình đào tạo", StatusCode.NOT_FOUND));

        Cohort cohort = new Cohort();
        cohort.setCohortCode(request.getCohortCode());
        cohort.setStartAcademicYear(request.getStartAcademicYear());
        cohort.setTotalStudyYears(
                request.getTotalStudyYears() != null ? request.getTotalStudyYears().byteValue() : (byte) 4);
        cohort.setCurriculum(curriculum);

        return cohortRepository.save(cohort);
    }

    public Cohort updateCohort(Long cohortId, CohortRequest request) {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new AppException("Không tìm thấy khóa học", StatusCode.NOT_FOUND));
        Curriculum curriculum = curriculumRepository.findById(request.getCurriculumId())
                .orElseThrow(() -> new AppException("Không tìm thấy chương trình đào tạo", StatusCode.NOT_FOUND));

        cohort.setCohortCode(request.getCohortCode());
        cohort.setStartAcademicYear(request.getStartAcademicYear());
        cohort.setTotalStudyYears(
                request.getTotalStudyYears() != null ? request.getTotalStudyYears().byteValue() : (byte) 4);
        cohort.setCurriculum(curriculum);

        return cohortRepository.save(cohort);
    }

    public void deleteCohort(Long cohortId) {
        if (!cohortRepository.existsById(cohortId)) {
            throw new AppException("Không tìm thấy khóa học", StatusCode.NOT_FOUND);
        }
        if (studentProfileRepository.existsByCohort_CohortId(cohortId)) {
            throw new AppException("Khóa học đã có sinh viên đăng ký hồ sơ, không thể xóa.",
                    StatusCode.EMAIL_ALREADY_IN_USE);
        }
        cohortRepository.deleteById(cohortId);
    }

    // ==========================================
    // ACADEMIC TERMS MANAGEMENT
    // ==========================================

    public List<AcademicTerm> getAllAcademicTerms() {
        return academicTermRepository.findAll();
    }

    public AcademicTerm createAcademicTerm(AcademicTermRequest request) {
        // Only allow 'planned' status when creating - use activate endpoint to activate
        String status = request.getStatus().toLowerCase();
        if ("active".equals(status)) {
            throw new AppException("Không thể tạo học kỳ với trạng thái đang hoạt động. Hãy tạo với trạng thái Dự kiến rồi kích hoạt sau.", StatusCode.EMAIL_ALREADY_IN_USE);
        }

        AcademicTerm term = new AcademicTerm();
        term.setAcademicYearStart(request.getAcademicYearStart());
        term.setAcademicYearEnd(request.getAcademicYearEnd());
        term.setSemesterNo(request.getSemesterNo());
        term.setFullName(request.getFullName());
        term.setStatus(status);
        return academicTermRepository.save(term);
    }

    public AcademicTerm updateAcademicTerm(Long termId, AcademicTermRequest request) {
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new AppException("Không tìm thấy học kỳ", StatusCode.NOT_FOUND));

        String newStatus = request.getStatus().toLowerCase();
        String currentStatus = (term.getStatus() != null) ? term.getStatus().toLowerCase() : "";

        // Prevent completed terms from being changed back to active or planned
        if ("completed".equals(currentStatus) && !"completed".equals(newStatus)) {
            throw new AppException("Học kỳ đã kết thúc không thể thay đổi trạng thái", StatusCode.EMAIL_ALREADY_IN_USE);
        }

        // Prevent setting active through update - must use activate endpoint
        if ("active".equals(newStatus) && !"active".equals(currentStatus)) {
            throw new AppException("Vui lòng sử dụng chức năng Kích hoạt để đặt học kỳ thành hoạt động", StatusCode.EMAIL_ALREADY_IN_USE);
        }

        term.setAcademicYearStart(request.getAcademicYearStart());
        term.setAcademicYearEnd(request.getAcademicYearEnd());
        term.setSemesterNo(request.getSemesterNo());
        term.setFullName(request.getFullName());
        term.setStatus(newStatus);
        return academicTermRepository.save(term);
    }

    @Transactional
    public void activateAcademicTerm(Long termId) {
        AcademicTerm targetedTerm = academicTermRepository.findById(termId)
                .orElseThrow(() -> new AppException("Không tìm thấy học kỳ", StatusCode.NOT_FOUND));

        // Prevent re-activating a completed term
        if ("completed".equalsIgnoreCase(targetedTerm.getStatus())) {
            throw new AppException("Học kỳ đã kết thúc không thể kích hoạt lại", StatusCode.EMAIL_ALREADY_IN_USE);
        }

        // Find active terms and change status to completed
        Optional<AcademicTerm> currentActiveOpt = academicTermRepository.findFirstByStatus("active");
        if (currentActiveOpt.isPresent()) {
            AcademicTerm currentActive = currentActiveOpt.get();
            if (!currentActive.getTermId().equals(termId)) {
                currentActive.setStatus("completed");
                academicTermRepository.save(currentActive);
            }
        }

        targetedTerm.setStatus("active");
        academicTermRepository.save(targetedTerm);
    }

    // ==========================================
    // STUDENT PROFILES MANAGEMENT
    // ==========================================

    public Page<StudentProfile> searchProfiles(String search, Long cohortId, Pageable pageable) {
        return studentProfileRepository.searchProfiles(search, cohortId, pageable);
    }

    public StudentProfile getProfileDetail(Long profileId) {
        return studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new AppException("Không tìm thấy hồ sơ sinh viên", StatusCode.NOT_FOUND));
    }

    public StudentProfile updateStudentProfile(Long profileId, Long cohortId, String studentCode, String fullName,
            String region, String gender) {
        StudentProfile profile = studentProfileRepository.findById(profileId)
                .orElseThrow(() -> new AppException("Không tìm thấy hồ sơ sinh viên", StatusCode.NOT_FOUND));

        if (cohortId != null) {
            Cohort cohort = cohortRepository.findById(cohortId)
                    .orElseThrow(() -> new AppException("Không tìm thấy khóa học", StatusCode.NOT_FOUND));
            profile.setCohort(cohort);
        }

        if (studentCode != null)
            profile.setStudentCode(studentCode);
        if (fullName != null)
            profile.setFullName(fullName);
        if (region != null)
            profile.setRegion(region);
        if (gender != null)
            profile.setGender(gender);

        return studentProfileRepository.save(profile);
    }

    public AcademicStatsResponse getAcademicStats() {
        Long totalStudents = studentProfileRepository.count();

        // Parse cohorts statistics
        Map<String, Long> cohortStats = new LinkedHashMap<>();
        List<Object[]> rawCohorts = studentProfileRepository.countStudentsPerCohort();
        for (Object[] row : rawCohorts) {
            String code = row[0] != null ? row[0].toString() : "Chưa phân khóa";
            Long count = row[1] != null ? (Long) row[1] : 0L;
            cohortStats.put(code, count);
        }

        // Parse regional statistics
        Map<String, Long> regionStats = new LinkedHashMap<>();
        List<Object[]> rawRegions = studentProfileRepository.countStudentsPerRegion();
        for (Object[] row : rawRegions) {
            String region = row[0] != null ? row[0].toString() : "Chưa thiết lập";
            Long count = row[1] != null ? (Long) row[1] : 0L;
            regionStats.put(region, count);
        }

        // Parse top subjects enrollments
        List<AcademicStatsResponse.SubjectEnrollmentStat> subjectStats = new ArrayList<>();
        List<Object[]> rawEnrollments = studentSubjectEnrollmentRepository.getTopSubjectsEnrollment();
        int countLimit = 0;
        for (Object[] row : rawEnrollments) {
            if (countLimit >= 5)
                break;
            String subName = row[0] != null ? row[0].toString() : "Môn học ẩn";
            Long count = row[1] != null ? (Long) row[1] : 0L;
            subjectStats.add(new AcademicStatsResponse.SubjectEnrollmentStat(subName, count));
            countLimit++;
        }

        return new AcademicStatsResponse(totalStudents, cohortStats, subjectStats, regionStats);
    }
}
