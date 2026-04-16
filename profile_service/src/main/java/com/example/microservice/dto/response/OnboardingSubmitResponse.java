package com.example.microservice.dto.response;
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
    public Long getProfileId() {
        return profileId;
    }
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getStudentTermProfileId() {
        return studentTermProfileId;
    }
    public void setStudentTermProfileId(Long studentTermProfileId) {
        this.studentTermProfileId = studentTermProfileId;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
