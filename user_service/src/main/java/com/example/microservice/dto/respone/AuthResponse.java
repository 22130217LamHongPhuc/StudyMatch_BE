package com.example.microservice.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private boolean onboardingCompleted;
    private Long userId;
    private boolean emailVerified;

}
