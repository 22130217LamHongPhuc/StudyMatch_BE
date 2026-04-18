package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OnboardingSubmitResponse {
    private Long profileId;
    private Long userId;
    private Long studentTermProfileId;
    private boolean success;
    private String message;
    public OnboardingSubmitResponse() {
    }
    public OnboardingSubmitResponse(Long profileId, Long studentTermProfileId, boolean success, String message) {
        this.profileId = profileId;
        this.studentTermProfileId = studentTermProfileId;
        this.success = success;
        this.message = message;
    }

}
