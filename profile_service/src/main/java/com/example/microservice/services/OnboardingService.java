package com.example.microservice.services;

import com.example.microservice.dto.request.OnboardingSubmitRequest;
import com.example.microservice.dto.response.OnboardingSubmitResponse;
import com.example.microservice.entity.*;
import com.example.microservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;

@Service
public class OnboardingService {

    @org.springframework.beans.factory.annotation.Value("${recommender.service.url:http://localhost:8000}")
    private String recommenderServiceUrl;

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
    private  RestTemplate restTemplate;

    @Autowired
    private com.example.microservice.clients.UserClient userClient;


    @Transactional
    public OnboardingSubmitResponse submitOnboarding(Long userId, OnboardingSubmitRequest request) {
        try {
            validateRequest(request);

            Cohort cohort = cohortRepository.findById(request.getCohortId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + request.getCohortId()));

            AcademicTerm term = academicTermRepository.findByStatus("active")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + request.getTermId()));

            StudentProfile studentProfile = studentProfileRepository.findByUserId(userId)
                    .orElse(new StudentProfile());

            studentProfile.setUserId(userId);
            studentProfile.setStudentCode(request.getStudentCode());
            
            String fullName = null;
            try {
                com.example.microservice.dto.response.ApiResponse<String> nameResponse = userClient.getFullName(userId);
                if (nameResponse != null && nameResponse.isSuccess()) {
                    fullName = nameResponse.getData();
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi lấy tên từ USER-SERVICE: " + e.getMessage());
                e.printStackTrace();
            }

            if (fullName == null || fullName.trim().isEmpty()) {
                throw new RuntimeException("Họ tên không được để trống (Không tìm thấy họ tên từ USER-SERVICE cho userId: " + userId + ")");
            }

            studentProfile.setFullName(fullName);
            studentProfile.setGender(request.getGender());
            studentProfile.setAgeGroup(request.getAgeGroup());
            studentProfile.setRegion(request.getRegion());
            studentProfile.setCohort(cohort);

            studentProfile = studentProfileRepository.save(studentProfile);

            StudentTermProfile studentTermProfile = studentTermProfileRepository
                    .findByUserIdAndTerm_TermId(userId, request.getTermId())
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

            studentTermProfile = studentTermProfileRepository.save(studentTermProfile);

            if (request.getCurrentSubjectIds() != null && !request.getCurrentSubjectIds().isEmpty()) {
                // Delete existing enrollments
//                List<StudentSubjectEnrollment> existingEnrollments = studentSubjectEnrollmentRepository
//                        .findByUserIdAndTermId(userId, request.getTermId());
//                studentSubjectEnrollmentRepository.deleteAll(existingEnrollments);

                for (Long subjectId : request.getCurrentSubjectIds()) {
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));

                    StudentSubjectEnrollment enrollment = new StudentSubjectEnrollment();
                    enrollment.setUserId(userId);
                    enrollment.setTerm(term);
                    enrollment.setSubject(subject);
                    studentSubjectEnrollmentRepository.save(enrollment);
                }
            }

            if (request.getFreeTimeSlots() != null && !request.getFreeTimeSlots().isEmpty()) {
                // Delete existing slots
//                List<StudentFreeTimeSlot> existingSlots = studentFreeTimeSlotRepository
//                        .findByUserIdAndTermId(userId, request.getTermId());
//                studentFreeTimeSlotRepository.deleteAll(existingSlots);

                // Create new slots
                for (OnboardingSubmitRequest.FreeTimeSlotDto slotDto : request.getFreeTimeSlots()) {
                    StudentFreeTimeSlot slot = new StudentFreeTimeSlot();
                    slot.setUserId(userId);
                    slot.setTerm(term);
                    slot.setDayOfWeek(slotDto.getDayOfWeek());
                    slot.setSlotCode(slotDto.getSlotCode());
                    slot.setIsAvailable(true);
                    studentFreeTimeSlotRepository.save(slot);
                }
            }

            // 7. Save subject schedule slots
            if (request.getSubjectScheduleSlots() != null && !request.getSubjectScheduleSlots().isEmpty()) {
                // Delete existing schedule slots
//                List<StudentSubjectScheduleSlot> existingSchedules = studentSubjectScheduleSlotRepository
//                        .findByUserIdAndTermId(userId, request.getTermId());
//                studentSubjectScheduleSlotRepository.deleteAll(existingSchedules);

                // Create new schedule slots
                for (OnboardingSubmitRequest.SubjectScheduleSlotDto scheduleDto : request.getSubjectScheduleSlots()) {
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

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            reloadAiRecommender(userId);
                        }
                    }
            );

            // 8. Return success response
            OnboardingSubmitResponse response = new OnboardingSubmitResponse(
                    studentProfile.getProfileId(),
                    studentTermProfile.getId(),
                    true,
                    "Onboarding thành công"
            );
            response.setUserId(userId);
            return response;

        } catch (Exception e) {
            OnboardingSubmitResponse response = new OnboardingSubmitResponse();
            response.setSuccess(false);
            response.setMessage("Lỗi khi onboarding: " + e.getMessage());
            return response;
        }
    }

    private void validateRequest(OnboardingSubmitRequest request) {
        if (request.getStudentCode() == null || request.getStudentCode().isEmpty()) {
            throw new RuntimeException("Mã sinh viên không được để trống");
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
            String url = recommenderServiceUrl + "/api/reload-recommender?userId=" + userId;

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

