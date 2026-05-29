package com.example.microservice.dto;
public class TokenValidateResponse {

    private boolean valid;
    private Long userId;
    private String username;
    private String message;

    public TokenValidateResponse() {
    }

    public TokenValidateResponse(boolean valid, Long userId, String username, String message) {
        this.valid = valid;
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    public static TokenValidateResponse valid(Long userId, String username) {
        return new TokenValidateResponse(true, userId, username, "Valid token");
    }

    public static TokenValidateResponse invalid(String message) {
        return new TokenValidateResponse(false, null, null, message);
    }

    public boolean isValid() {
        return valid;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}