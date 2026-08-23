package com.example.microservice.services;

import com.example.microservice.dto.response.*;
import com.example.microservice.entity.*;
import com.example.microservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileLoadService {

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
    private SubjectRepository subjectRepository;

    @Autowired
    private AcademicTermRepository academicTermRepository;

    public UserProfileFullResponse loadUserProfile(Long userId) {
        UserProfileFullResponse response = new UserProfileFullResponse();

        try {
            Optional<StudentProfile> profileOptional = studentProfileRepository.findByUserId(userId);
            if (profileOptional.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("User profile not found");
                return response;
            }

            StudentProfile studentProfile = profileOptional.get();
            StudentProfileDetailResponse profileDetail = convertToProfileDetailResponse(studentProfile);
            response.setProfile(profileDetail);

            List<StudentTermProfile> termProfiles = studentTermProfileRepository.findByUserId(userId);
            List<StudentTermProfileDetailResponse> termProfileResponses = termProfiles.stream()
                    .map(termProfile -> convertToTermProfileDetailResponse(termProfile))
                    .collect(Collectors.toList());
            response.setTermProfiles(termProfileResponses);

            AcademicTerm activeTerm = academicTermRepository.findFirstByStatusIgnoreCase("active")
                    .or(() -> academicTermRepository.findFirstByStatus("active"))
                    .or(() -> academicTermRepository.findFirstByStatus("ACTIVE"))
                    .or(() -> academicTermRepository.findByStatusIgnoreCase("active"))
                    .orElse(null);

            Long activeTermId = (activeTerm != null) ? activeTerm.getTermId() : null;

            List<StudentSubjectEnrollment> enrollments = (activeTermId != null)
                    ? studentSubjectEnrollmentRepository.findByUserIdAndTerm_TermId(userId, activeTermId)
                    : studentSubjectEnrollmentRepository.findByUserId(userId);
            List<StudentSubjectEnrollmentResponse> enrollmentResponses = enrollments.stream()
                    .map(enrollment -> convertToEnrollmentResponse(enrollment))
                    .collect(Collectors.toList());
            response.setEnrollments(enrollmentResponses);

            List<StudentFreeTimeSlot> freeTimeSlots = (activeTermId != null)
                    ? studentFreeTimeSlotRepository.findByUserIdAndTerm_TermId(userId, activeTermId)
                    : studentFreeTimeSlotRepository.findByUserId(userId);
            List<FreeTimeSlotResponse> freeTimeSlotResponses = freeTimeSlots.stream()
                    .map(slot -> convertToFreeTimeSlotResponse(slot))
                    .collect(Collectors.toList());
            response.setFreeTimeSlots(freeTimeSlotResponses);

            List<StudentSubjectScheduleSlot> scheduleSlots = (activeTermId != null)
                    ? studentSubjectScheduleSlotRepository.findByUserIdAndTerm_TermId(userId, activeTermId)
                    : studentSubjectScheduleSlotRepository.findByUserId(userId);
            List<ScheduleSlotResponse> scheduleSlotResponses = scheduleSlots.stream()
                    .map(slot -> convertToScheduleSlotResponse(slot))
                    .collect(Collectors.toList());
            response.setScheduleSlots(scheduleSlotResponses);

            response.setSuccess(true);

            response.setMessage("User profile loaded successfully");

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error loading user profile: " + e.getMessage());
        }

        return response;
    }

    private StudentProfileDetailResponse convertToProfileDetailResponse(StudentProfile studentProfile) {
        StudentProfileDetailResponse response = new StudentProfileDetailResponse(
                studentProfile.getProfileId(),
                studentProfile.getUserId(),
                studentProfile.getStudentCode(),
                studentProfile.getFullName(),
                studentProfile.getGender(),
                studentProfile.getAgeGroup(),
                studentProfile.getRegion(),
                studentProfile.getCreatedAt());

        if (studentProfile.getCohort() != null) {
            Cohort cohort = studentProfile.getCohort();
            CohortInfoResponse cohortResponse = new CohortInfoResponse(
                    cohort.getCohortId(),
                    cohort.getCohortCode(),
                    cohort.getStartAcademicYear(),
                    cohort.getTotalStudyYears());

            if (cohort.getCurriculum() != null) {
                Curriculum curriculum = cohort.getCurriculum();
                CurriculumInfoResponse curriculumResponse = new CurriculumInfoResponse(
                        curriculum.getCurriculumId(),
                        curriculum.getCurriculumCode(),
                        curriculum.getCurriculumName());
                cohortResponse.setCurriculum(curriculumResponse);
            }

            response.setCohort(cohortResponse);
        }

        return response;
    }

    private StudentTermProfileDetailResponse convertToTermProfileDetailResponse(StudentTermProfile termProfile) {
        String mainSubjectName = null;
        if (termProfile.getMainSubjectId() != null) {
            Optional<Subject> subject = subjectRepository.findById(termProfile.getMainSubjectId());
            if (subject.isPresent()) {
                mainSubjectName = subject.get().getSubjectName();
            }
        }

        StudentTermProfileDetailResponse response = new StudentTermProfileDetailResponse(
                termProfile.getId(),
                termProfile.getUserId(),
                termProfile.getStudyYearNo(),
                termProfile.getSemesterNo(),
                termProfile.getAvgScore(),
                termProfile.getStudiedCredits(),
                termProfile.getStudyGoal(),
                termProfile.getStudyMode(),
                termProfile.getMainSubjectId(),
                mainSubjectName);

        if (termProfile.getTerm() != null) {
            AcademicTerm term = termProfile.getTerm();
            AcademicTermResponse termResponse = new AcademicTermResponse(
                    term.getTermId(),
                    term.getAcademicYearStart(),
                    term.getAcademicYearEnd(),
                    term.getSemesterNo(),
                    term.getFullName(),
                    term.getStatus());
            response.setTerm(termResponse);
        }

        return response;
    }

    private StudentSubjectEnrollmentResponse convertToEnrollmentResponse(StudentSubjectEnrollment enrollment) {
        StudentSubjectEnrollmentResponse response = new StudentSubjectEnrollmentResponse(enrollment.getId());

        if (enrollment.getSubject() != null) {
            Subject subject = enrollment.getSubject();
            SubjectInfoResponse subjectResponse = new SubjectInfoResponse(
                    subject.getSubjectId(),
                    subject.getSubjectCode(),
                    subject.getSubjectName());
            response.setSubject(subjectResponse);
        }

        if (enrollment.getTerm() != null) {
            AcademicTerm term = enrollment.getTerm();
            AcademicTermResponse termResponse = new AcademicTermResponse(
                    term.getTermId(),
                    term.getAcademicYearStart(),
                    term.getAcademicYearEnd(),
                    term.getSemesterNo(),
                    term.getFullName(),
                    term.getStatus());
            response.setTerm(termResponse);
        }

        return response;
    }

    private FreeTimeSlotResponse convertToFreeTimeSlotResponse(StudentFreeTimeSlot freeTimeSlot) {
        return new FreeTimeSlotResponse(
                freeTimeSlot.getId(),
                freeTimeSlot.getDayOfWeek(),
                freeTimeSlot.getSlotCode(),
                freeTimeSlot.getIsAvailable());
    }

    private ScheduleSlotResponse convertToScheduleSlotResponse(StudentSubjectScheduleSlot scheduleSlot) {
        ScheduleSlotResponse response = new ScheduleSlotResponse(
                scheduleSlot.getId(),
                scheduleSlot.getDayOfWeek(),
                scheduleSlot.getSlotCode(),
                scheduleSlot.getScheduleType(),
                scheduleSlot.getLocation(),
                scheduleSlot.getNote());

        if (scheduleSlot.getSubject() != null) {
            Subject subject = scheduleSlot.getSubject();
            SubjectInfoResponse subjectResponse = new SubjectInfoResponse(
                    subject.getSubjectId(),
                    subject.getSubjectCode(),
                    subject.getSubjectName());
            response.setSubject(subjectResponse);
        }

        return response;
    }

    public TermUpdateStatusResponse getTermUpdateStatus(Long userId) {
        TermUpdateStatusResponse status = new TermUpdateStatusResponse();
        status.setNeedsUpdate(false);

        Optional<AcademicTerm> activeTermOpt = academicTermRepository.findFirstByStatus("active");
        if (activeTermOpt.isEmpty()) {
            return status;
        }

        AcademicTerm activeTerm = activeTermOpt.get();
        status.setActiveTermId(activeTerm.getTermId());
        status.setActiveTermName(activeTerm.getFullName());

        Optional<StudentTermProfile> termProfileOpt = studentTermProfileRepository
                .findByUserIdAndTerm_TermId(userId, activeTerm.getTermId());

        if (termProfileOpt.isEmpty()) {
            status.setNeedsUpdate(true);

            List<StudentTermProfile> allTermProfiles = studentTermProfileRepository.findByUserId(userId);
            if (!allTermProfiles.isEmpty()) {
                StudentTermProfile lastProfile = allTermProfiles.get(allTermProfiles.size() - 1);
                AcademicTerm lastTerm = lastProfile.getTerm();
                if (lastTerm != null) {
                    status.setLastUpdatedTermId(lastTerm.getTermId());
                    status.setLastUpdatedTermName(lastTerm.getFullName());
                }
            }
        }

        return status;
    }
}
