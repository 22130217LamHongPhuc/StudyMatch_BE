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


    @Transactional(rollbackFor = Exception.class)
    public OnboardingSubmitResponse submitOnboarding(Long userId, OnboardingSubmitRequest request) {
        validateRequest(userId, request);

        String studentCode = request.getStudentCode().trim();

        // 1. Kiểm tra trùng mã sinh viên với tài khoản khác
        java.util.Optional<StudentProfile> existingWithCode = studentProfileRepository.findByStudentCode(studentCode);
        if (existingWithCode.isPresent() && !existingWithCode.get().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Mã sinh viên '" + studentCode + "' đã tồn tại trong hệ thống. Vui lòng kiểm tra lại.");
        }

        // 2. Kiểm tra khóa học
        Cohort cohort = cohortRepository.findById(request.getCohortId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khóa học với ID: " + request.getCohortId()));

        // 3. Lấy học kỳ đang hoạt động (ACTIVE) trực tiếp từ database (không phụ thuộc vào frontend)
        AcademicTerm term = academicTermRepository.findFirstByStatusIgnoreCase("active")
                .or(() -> academicTermRepository.findFirstByStatus("active"))
                .or(() -> academicTermRepository.findFirstByStatus("ACTIVE"))
                .or(() -> academicTermRepository.findByStatusIgnoreCase("active"))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học kỳ đang hoạt động trong hệ thống."));

        // 4. Lấy họ tên người dùng từ USER-SERVICE
        String fullName = null;
        try {
            com.example.microservice.dto.response.ApiResponse<String> nameResponse = userClient.getFullName(userId);
            if (nameResponse != null && nameResponse.isSuccess()) {
                fullName = nameResponse.getData();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy tên từ USER-SERVICE: " + e.getMessage());
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = request.getFullName();
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống (Không tìm thấy thông tin tài khoản)");
        }

        // 5. Kiểm tra trước các môn học tồn tại
        if (request.getCurrentSubjectIds() != null) {
            for (Long subjectId : request.getCurrentSubjectIds()) {
                if (subjectId != null && !subjectRepository.existsById(subjectId)) {
                    throw new IllegalArgumentException("Môn học với ID " + subjectId + " không tồn tại trong hệ thống.");
                }
            }
        }

        if (request.getSubjectScheduleSlots() != null) {
            for (OnboardingSubmitRequest.SubjectScheduleSlotDto scheduleDto : request.getSubjectScheduleSlots()) {
                if (scheduleDto.getSubjectId() != null && !subjectRepository.existsById(scheduleDto.getSubjectId())) {
                    throw new IllegalArgumentException("Môn học trong thời khóa biểu với ID " + scheduleDto.getSubjectId() + " không tồn tại.");
                }
            }
        }

        // 6. Lưu hoặc cập nhật StudentProfile
        StudentProfile studentProfile = studentProfileRepository.findByUserId(userId)
                .orElse(new StudentProfile());
        studentProfile.setUserId(userId);
        studentProfile.setStudentCode(studentCode);
        studentProfile.setFullName(fullName);
        studentProfile.setGender(request.getGender());
        studentProfile.setAgeGroup(request.getAgeGroup());
        studentProfile.setRegion(request.getRegion());
        studentProfile.setCohort(cohort);
        studentProfile = studentProfileRepository.save(studentProfile);

        // 7. Lưu hoặc cập nhật StudentTermProfile
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
        studentTermProfile = studentTermProfileRepository.save(studentTermProfile);

        // 8. Làm sạch và lưu danh sách môn học, thời gian rảnh, lịch học
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

        // 9. Trả về kết quả thành công
        OnboardingSubmitResponse response = new OnboardingSubmitResponse(
                studentProfile.getProfileId(),
                studentTermProfile.getId(),
                true,
                "Onboarding thành công"
        );
        response.setUserId(userId);
        return response;
    }

    private void replaceEnrollments(Long userId, AcademicTerm term, OnboardingSubmitRequest request) {
        java.util.List<StudentSubjectEnrollment> existing = studentSubjectEnrollmentRepository
                .findByUserIdAndTerm_TermId(userId, term.getTermId());
        if (!existing.isEmpty()) {
            studentSubjectEnrollmentRepository.deleteAll(existing);
            studentSubjectEnrollmentRepository.flush();
        }

        if (request.getCurrentSubjectIds() == null || request.getCurrentSubjectIds().isEmpty()) {
            return;
        }

        java.util.Set<Long> uniqueIds = new java.util.LinkedHashSet<>(request.getCurrentSubjectIds());
        for (Long subjectId : uniqueIds) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy môn học với ID: " + subjectId));

            StudentSubjectEnrollment enrollment = new StudentSubjectEnrollment();
            enrollment.setUserId(userId);
            enrollment.setTerm(term);
            enrollment.setSubject(subject);
            studentSubjectEnrollmentRepository.save(enrollment);
        }
    }

    private void replaceFreeTimeSlots(Long userId, AcademicTerm term, OnboardingSubmitRequest request) {
        java.util.List<StudentFreeTimeSlot> existing = studentFreeTimeSlotRepository
                .findByUserIdAndTerm_TermId(userId, term.getTermId());
        if (!existing.isEmpty()) {
            studentFreeTimeSlotRepository.deleteAll(existing);
            studentFreeTimeSlotRepository.flush();
        }

        if (request.getFreeTimeSlots() == null || request.getFreeTimeSlots().isEmpty()) {
            return;
        }

        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
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
        java.util.List<StudentSubjectScheduleSlot> existing = studentSubjectScheduleSlotRepository
                .findByUserIdAndTerm_TermId(userId, term.getTermId());
        if (!existing.isEmpty()) {
            studentSubjectScheduleSlotRepository.deleteAll(existing);
            studentSubjectScheduleSlotRepository.flush();
        }

        if (request.getSubjectScheduleSlots() == null || request.getSubjectScheduleSlots().isEmpty()) {
            return;
        }

        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
        for (OnboardingSubmitRequest.SubjectScheduleSlotDto scheduleDto : request.getSubjectScheduleSlots()) {
            String key = scheduleDto.getSubjectId() + "|" + scheduleDto.getDayOfWeek() + "|" + scheduleDto.getSlotCode() + "|" + scheduleDto.getScheduleType();
            if (!seen.add(key)) {
                continue;
            }
            Subject subject = subjectRepository.findById(scheduleDto.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy môn học với ID: " + scheduleDto.getSubjectId()));

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

    private void validateRequest(Long userId, OnboardingSubmitRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID không hợp lệ.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu onboarding không được để trống.");
        }
        if (request.getStudentCode() == null || request.getStudentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống.");
        }
        if (request.getCohortId() == null) {
            throw new IllegalArgumentException("Khóa học không được để trống.");
        }
        if (request.getStudyYearNo() == null) {
            throw new IllegalArgumentException("Năm học không được để trống.");
        }
        if (request.getSemesterNo() == null) {
            throw new IllegalArgumentException("Học kỳ không được để trống.");
        }
    }

    public boolean isStudentCodeExists(String studentCode, Long currentUserId) {
        if (studentCode == null || studentCode.trim().isEmpty()) {
            return false;
        }
        String trimmed = studentCode.trim();
        java.util.Optional<StudentProfile> profileOpt = studentProfileRepository.findByStudentCode(trimmed);
        return profileOpt.isPresent() && (currentUserId == null || !profileOpt.get().getUserId().equals(currentUserId));
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

