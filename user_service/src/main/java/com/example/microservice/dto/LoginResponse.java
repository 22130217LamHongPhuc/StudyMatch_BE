package com.example.microservice.dto;

public class LoginResponse {
    private int userId;
    private String token;
    private String email;
    private String username;
    private String avatar;

    public LoginResponse() {
    }
    public LoginResponse(int userId,String username,String token, String email, String avatar) {
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.email = email;
        this.avatar = avatar ;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
