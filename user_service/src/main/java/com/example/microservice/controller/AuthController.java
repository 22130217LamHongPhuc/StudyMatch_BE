package com.example.microservice.controller;


import com.example.microservice.dto.request.*;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.AuthResponse;
import com.example.microservice.entity.EmailVerificationToken;
import com.example.microservice.entity.PasswordResetToken;
import com.example.microservice.entity.RefreshToken;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.UserRepository;
import com.example.microservice.service.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GoogleAuthService googleAuthService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired EmailVerificationTokenService verificationTokenService;
    @Autowired AuthService authService;

    @GetMapping("/test")
    public String test() {
        return "Hello";
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already in use", StatusCode.EMAIL_ALREADY_IN_USE);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("student");
        user.setStatus("ACTIVE");
        user.setEmailVerified(false);
        user.setOnboardingCompleted(false);
        userRepository.save(user);

//        String token = jwtService.generateToken(new CustomUserDetails(user));
//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);


        EmailVerificationToken verificationToken = verificationTokenService.saveVerificationToken(user);

        String link = "http://localhost:8085/api/verify-email/confirm?token=" + verificationToken.getToken();


        mailService.sendMailTo(user.getEmail(), "Chào mừng đến với StudyMatch",
                "verify-email",
                 link
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Registration successful",
                new AuthResponse("token", "",user.isOnboardingCompleted(),user.getUserId(),false)
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found with email: " + request.getEmail(), StatusCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new AppException("Password is incorrect", StatusCode.PASSWORD_INCORRECT);
            }
        System.out.println("start auth : "+user.getEmail());

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(user));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Login successful",
                new AuthResponse(token, refreshToken.getToken(),user.isOnboardingCompleted(),user.getUserId(),user.isEmailVerified())
        ));
    }

    @PostMapping("/google")
    public ApiResponse<AuthResponse> loginGoogle(
            @RequestBody GoogleLoginRequest request
    ) {
        GoogleIdToken.Payload payload =
                googleAuthService.verifyIdToken(request.getIdToken());

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        User user = googleAuthService.findOrCreateGoogleUser(
                email,
                name,
                picture
        );

        String accessToken = jwtService.generateToken(new CustomUserDetails(user));
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Google login successful",
                new AuthResponse(accessToken, refreshToken,user.isOnboardingCompleted(),user.getUserId(),true)
        );
    }

    @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        System.out.println("refresh token "+request.getRefreshToken());
        RefreshToken rt = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = rt.getUser();
        String token = jwtService.generateToken(new CustomUserDetails(user));

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Refresh token successful",
                new AuthResponse(token, rt.getToken(),user.isOnboardingCompleted(),user.getUserId(),true)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody RefreshTokenRequest tokenRequest) {
        refreshTokenService.revokeRefreshToken(tokenRequest.getRefreshToken());
        return  ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Logout successful", null));
    }

    @PostMapping("/complete-onboarding/{userId}")
    public ResponseEntity<ApiResponse<String>> completeOnboarding(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException("User not found with id: " + userId, StatusCode.USER_NOT_FOUND)
        );
        user.setOnboardingCompleted(true);
        userRepository.save(user);
        return  ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Onboarding completed", null));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException("User not found with email: " + request.getEmail(), StatusCode.USER_NOT_FOUND)
        );

        PasswordResetToken resetToken = authService.saveForgetPasswordToken(user);

        String link = "http://localhost:3000/reset-password?token=" + resetToken.getToken();

        mailService.sendMailTo(user.getEmail(), "Yêu cầu đặt lại mật khẩu",
                "reset-password",
                 link
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Password reset email sent",
                null
        ));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request){
        authService.resetPassword(request.getNewPassword(), request.getToken());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Password reset successful",
                null
        ));
    }

    @PostMapping("/reset-verify-email")
    public ResponseEntity<ApiResponse<String>> resetVerifyEmail(@RequestBody ForgotPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException("User not found with email: " + request.getEmail(), StatusCode.USER_NOT_FOUND)
        );
        EmailVerificationToken verificationToken = verificationTokenService.saveVerificationToken(user);

        String link = "http://localhost:8085/api/verify-email/confirm?token=" + verificationToken.getToken();


        mailService.sendMailTo(user.getEmail(), "Chào mừng đến với StudyMatch",
                "verify-email",
                link
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Verification email sent",
                null
        ));
    }




}