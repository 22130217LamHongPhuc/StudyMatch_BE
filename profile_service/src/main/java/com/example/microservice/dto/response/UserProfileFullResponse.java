package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfileFullResponse {
    private StudentProfileDetailResponse profile;
    private List<StudentTermProfileDetailResponse> termProfiles;
    private List<StudentSubjectEnrollmentResponse> enrollments;
    private List<FreeTimeSlotResponse> freeTimeSlots;
    private List<ScheduleSlotResponse> scheduleSlots;
    private boolean success;
    private String message;

    public UserProfileFullResponse() {
        this.success = true;
    }

    public UserProfileFullResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }


}

