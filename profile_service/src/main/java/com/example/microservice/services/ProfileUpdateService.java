package com.example.microservice.services;

import com.example.microservice.dto.request.OnboardingSubmitRequest;
import com.example.microservice.dto.response.UserProfileFullResponse;
import com.example.microservice.entity.AcademicTerm;
import com.example.microservice.entity.Cohort;
import com.example.microservice.entity.StudentFreeTimeSlot;
import com.example.microservice.entity.StudentProfile;
import com.example.microservice.entity.StudentSubjectEnrollment;
import com.example.microservice.entity.StudentSubjectScheduleSlot;
import com.example.microservice.entity.StudentTermProfile;
import com.example.microservice.entity.Subject;
import com.example.microservice.repositories.AcademicTermRepository;
import com.example.microservice.repositories.CohortRepository;
import com.example.microservice.repositories.StudentFreeTimeSlotRepository;
import com.example.microservice.repositories.StudentProfileRepository;
import com.example.microservice.repositories.StudentSubjectEnrollmentRepository;
import com.example.microservice.repositories.StudentSubjectScheduleSlotRepository;
import com.example.microservice.repositories.StudentTermProfileRepository;
import com.example.microservice.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProfileUpdateService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentTermProfileRepository studentTermProfileRepository;

    @Autowired
    private StudentSubjectEnrollmentRepository studentSubjectEnrollmentRepository;

    @Autowired
    private StudentFreeTimeSlotRepository studentFreeTimeSlotRepository;

    @Autowired
    private StudentSubjectScheduleSlotRepository studentSubjectScheduleSlotRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private AcademicTermRepository academicTermRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ProfileLoadService profileLoadService;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public UserProfileFullResponse updateProfile(Long userId, OnboardingSubmitRequest request) {

        try {
            validateRequest(request);

            System.out.println(request);

            Cohort cohort = cohortRepository.findById(request.getCohortId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + request.getCohortId()));

            AcademicTerm term = academicTermRepository.findByStatus("ACTIVE")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + request.getTermId()));

            StudentProfile studentProfile = studentProfileRepository.findByUserId(userId)
                    .orElse(new StudentProfile());

            studentProfile.setUserId(userId);
            studentProfile.setStudentCode(request.getStudentCode());
            studentProfile.setFullName(request.getFullName());
            studentProfile.setGender(request.getGender());
            studentProfile.setAgeGroup(request.getAgeGroup());
            studentProfile.setRegion(request.getRegion());
            studentProfile.setCohort(cohort);
            studentProfileRepository.save(studentProfile);

            StudentTermProfile studentTermProfile = studentTermProfileRepository
                    .findByUserIdAndTerm_TermId(userId, term.getTermId())
                    .orElse(new StudentTermProfile());

            studentTermProfile.setUserId(userId);
            studentTermProfile.setTerm(term);
            studentTermProfile.setStudyYearNo(request.getStudyYearNo());
            studentTermProfile.setSemesterNo(request.getSemesterNo());
            studentTermProfile.setAvgScore(request.getAvgScore());
            studentTermProfile.setStudiedCredits(request.getStudiedCredits());
            studentTermProfile.setStudyGoal(request.getStudyGoal());
            studentTermProfile.setStudyMode(request.getStudyMode());
            studentTermProfile.setMainSubjectId(request.getMainSubjectId());
            studentTermProfileRepository.save(studentTermProfile);

            replaceEnrollments(userId, term, request);
            replaceFreeTimeSlots(userId, term, request);
            replaceSubjectScheduleSlots(userId, term, request);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            reloadAiRecommender(userId);
                        }
                    }
            );


            UserProfileFullResponse response = profileLoadService.loadUserProfile(userId);
            if (response.isSuccess()) {
                response.setMessage("Cập nhật profile thành công");
            }
            return response;
        } catch (Exception e) {
            UserProfileFullResponse response = new UserProfileFullResponse();
            response.setSuccess(false);
            response.setMessage("Lỗi khi cập nhật profile: " + e.getMessage());
            return response;
        }
    }

    private void replaceEnrollments(Long userId, AcademicTerm term, OnboardingSubmitRequest request) {
        List<StudentSubjectEnrollment> existingEnrollments =
                studentSubjectEnrollmentRepository.findByUserIdAndTerm_TermId(userId, term.getTermId());
        if (!existingEnrollments.isEmpty()) {
            studentSubjectEnrollmentRepository.deleteAll(existingEnrollments);
            studentSubjectEnrollmentRepository.flush();
        }

        if (request.getCurrentSubjectIds() == null || request.getCurrentSubjectIds().isEmpty()) {
            return;
        }

        Set<Long> uniqueSubjectIds = new LinkedHashSet<>(request.getCurrentSubjectIds());
        for (Long subjectId : uniqueSubjectIds) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));

            StudentSubjectEnrollment enrollment = new StudentSubjectEnrollment();
            enrollment.setUserId(userId);
            enrollment.setTerm(term);
            enrollment.setSubject(subject);
            studentSubjectEnrollmentRepository.save(enrollment);
        }
    }

    private void replaceFreeTimeSlots(Long userId, AcademicTerm term, OnboardingSubmitRequest request) {
        List<StudentFreeTimeSlot> existingSlots =
                studentFreeTimeSlotRepository.findByUserIdAndTerm_TermId(userId, term.getTermId());
        if (!existingSlots.isEmpty()) {
            studentFreeTimeSlotRepository.deleteAll(existingSlots);
            studentFreeTimeSlotRepository.flush();
        }

        if (request.getFreeTimeSlots() == null || request.getFreeTimeSlots().isEmpty()) {
            return;
        }

        Set<String> seen = new LinkedHashSet<>();
        for (OnboardingSubmitRequest.FreeTimeSlotDto slotDto : request.getFreeTimeSlots()) {
            String key = slotDto.getDayOfWeek() + "|" + slotDto.getSlotCode();
            if (!seen.add(key)) {
                continue;
            }
            StudentFreeTimeSlot slot = new StudentFreeTimeSlot();
            slot.setUserId(userId);
            slot.setTerm(term);
            slot.setDayOfWeek(slotDto.getDayOfWeek());
            slot.setSlotCode(slotDto.getSlotCode());
            slot.setIsAvailable(true);
            studentFreeTimeSlotRepository.save(slot);
        }
    }

    private void replaceSubjectScheduleSlots(Long userId, AcademicTerm term, OnboardingSubmitRequest request) {
        List<StudentSubjectScheduleSlot> existingSchedules =
                studentSubjectScheduleSlotRepository.findByUserIdAndTerm_TermId(userId, term.getTermId());
        if (!existingSchedules.isEmpty()) {
            studentSubjectScheduleSlotRepository.deleteAll(existingSchedules);
            studentSubjectScheduleSlotRepository.flush();
        }

        if (request.getSubjectScheduleSlots() == null || request.getSubjectScheduleSlots().isEmpty()) {
            return;
        }

        Set<String> seen = new LinkedHashSet<>();
        for (OnboardingSubmitRequest.SubjectScheduleSlotDto scheduleDto : request.getSubjectScheduleSlots()) {
            String key = scheduleDto.getSubjectId() + "|" + scheduleDto.getDayOfWeek() + "|" + scheduleDto.getSlotCode() + "|" + scheduleDto.getScheduleType();
            if (!seen.add(key)) {
                continue;
            }
            Subject subject = subjectRepository.findById(scheduleDto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + scheduleDto.getSubjectId()));

            StudentSubjectScheduleSlot scheduleSlot = new StudentSubjectScheduleSlot();
            scheduleSlot.setUserId(userId);
            scheduleSlot.setTerm(term);
            scheduleSlot.setSubject(subject);
            scheduleSlot.setDayOfWeek(scheduleDto.getDayOfWeek());
            scheduleSlot.setSlotCode(scheduleDto.getSlotCode());
            scheduleSlot.setScheduleType(scheduleDto.getScheduleType());
            studentSubjectScheduleSlotRepository.save(scheduleSlot);
        }
    }

    private void validateRequest(OnboardingSubmitRequest request) {
        if (request.getStudentCode() == null || request.getStudentCode().isEmpty()) {
            throw new RuntimeException("Mã sinh viên không được để trống");
        }
        if (request.getFullName() == null || request.getFullName().isEmpty()) {
            throw new RuntimeException("Họ tên không được để trống");
        }
        if (request.getCohortId() == null) {
            throw new RuntimeException("ID khóa học không được để trống");
        }
        if (request.getTermId() == null) {
            throw new RuntimeException("ID học kỳ không được để trống");
        }
        if (request.getStudyYearNo() == null) {
            throw new RuntimeException("Năm học không được để trống");
        }
        if (request.getSemesterNo() == null) {
            throw new RuntimeException("Học kỳ không được để trống");
        }
    }

    private void reloadAiRecommender(Long userId) {
        try {
            String url = "http://localhost:8000/api/reload-recommender?userId=" + userId;

            String response = restTemplate.postForObject(
                    url,
                    null,
                    String.class
            );
            System.out.println("AI reload response: " + response);
        } catch (Exception e) {
            System.err.println("Không thể reload AI recommender: " + e.getMessage());
        }
    }
}
