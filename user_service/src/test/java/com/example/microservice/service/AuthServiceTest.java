package com.example.microservice.service;

import com.example.microservice.dto.request.AuthRequest;
import com.example.microservice.dto.respone.AuthResponse;
import com.example.microservice.entity.RefreshToken;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.PasswordResetTokenRepository;
import com.example.microservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordResetTokenRepository resetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginAdminShouldReturnTokensWhenUserIsAdmin() {
        AuthRequest request = new AuthRequest("admin@studymatch.com", "secret");
        User user = buildUser("admin");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        AuthResponse response = authService.loginAdmin(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(user.getUserId(), response.getUserId());
        assertTrue(response.isEmailVerified());
        assertNotNull(user.getLastLoginAt());
        verify(userRepository).save(user);
    }

    @Test
    void loginAdminShouldRejectNonAdminUser() {
        AuthRequest request = new AuthRequest("student@studymatch.com", "secret");
        User user = buildUser("student");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authService.loginAdmin(request));

        assertEquals(StatusCode.ACCESS_DENIED, exception.getCode());
        verify(refreshTokenService, never()).createRefreshToken(any());
        verify(jwtService, never()).generateToken(any(CustomUserDetails.class));
        verify(userRepository, never()).save(user);
    }

    private User buildUser(String role) {
        User user = new User();
        user.setUserId(1L);
        user.setEmail(role + "@studymatch.com");
        user.setPasswordHash("encoded-password");
        user.setRole(role);
        user.setEmailVerified(true);
        user.setOnboardingCompleted(true);
        return user;
    }
}
