package com.example.microservice.controller;

import com.example.microservice.dto.request.*;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.AuthResponse;
import com.example.microservice.dto.respone.TokenValidateResponse;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @org.springframework.beans.factory.annotation.Value("${app.user-service.url:http://localhost:8085}")
    private String userServiceUrl;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Autowired
    CustomUserDetailService userDetailsService;
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
    @Autowired 
    EmailVerificationTokenService verificationTokenService;
    @Autowired 
    AuthService authService;


    @GetMapping("/test")
    public String test() {
        return "Xin chào";
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email đã được sử dụng", StatusCode.EMAIL_ALREADY_IN_USE);
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("student");
        user.setStatus("ACTIVE");
        user.setEmailVerified(false);
        user.setOnboardingCompleted(false);
        userRepository.save(user);

        EmailVerificationToken verificationToken = verificationTokenService.saveVerificationToken(user);

        String link = userServiceUrl + "/api/verify-email/confirm?token=" + verificationToken.getToken();


        mailService.sendMailTo(user.getEmail(), "Chào mừng đến với StudyMatch",
                "verify-email",
                 link
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Đăng ký thành công",
                new AuthResponse("token", "",user.isOnboardingCompleted(),user.getUserId(),false)
        ));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Đăng nhập thành công",
                response
        ));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<AuthResponse>> adminLogin(@RequestBody AuthRequest request) {
        AuthResponse response = authService.loginAdmin(request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Đăng nhập quản trị viên thành công",
                response
        ));
    }

    @PostMapping("/google")
    public ApiResponse<AuthResponse> loginGoogle(
            @RequestBody GoogleLoginRequest request
    ) {
        GoogleIdToken.Payload payload =
                googleAuthService.verifyIdToken(request.getIdToken());

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
                "Đăng nhập Google thành công",
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
                "Làm mới token thành công",
                new AuthResponse(token, rt.getToken(),user.isOnboardingCompleted(),user.getUserId(),true)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody RefreshTokenRequest tokenRequest) {
        refreshTokenService.revokeRefreshToken(tokenRequest.getRefreshToken());
        return  ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Đăng xuất thành công", null));
    }

    @PostMapping("/complete-onboarding/{userId}")
    public ResponseEntity<ApiResponse<String>> completeOnboarding(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException("Không tìm thấy người dùng với ID: " + userId, StatusCode.USER_NOT_FOUND)
        );
        user.setOnboardingCompleted(true);
        userRepository.save(user);
        return  ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Hoàn tất onboarding thành công", null));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException("Không tìm thấy người dùng với email: " + request.getEmail(), StatusCode.USER_NOT_FOUND)
        );

        PasswordResetToken resetToken = authService.saveForgetPasswordToken(user);

        String link = frontendUrl + "/reset-password?token=" + resetToken.getToken();

        mailService.sendMailTo(user.getEmail(), "Yêu cầu đặt lại mật khẩu",
                "reset-password",
                 link
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Đã gửi email đặt lại mật khẩu",
                null
        ));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request){
        authService.resetPassword(request.getNewPassword(), request.getToken());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Đặt lại mật khẩu thành công",
                null
        ));
    }

    @PostMapping("/reset-verify-email")
    public ResponseEntity<ApiResponse<String>> resetVerifyEmail(@RequestBody ForgotPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException("Không tìm thấy người dùng với email: " + request.getEmail(), StatusCode.USER_NOT_FOUND)
        );
        EmailVerificationToken verificationToken = verificationTokenService.saveVerificationToken(user);

        String link = userServiceUrl + "/api/verify-email/confirm?token=" + verificationToken.getToken();


        mailService.sendMailTo(user.getEmail(), "Chào mừng đến với StudyMatch",
                "verify-email",
                link
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Đã gửi lại email xác thực",
                null
        ));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<TokenValidateResponse> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.ok(TokenValidateResponse.invalid("Thiếu token"));
        }
        String token = authorization.substring(7);
       String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);



        try {
            boolean valid = jwtService.isTokenValid(token, userDetails);
            if (!valid) {
                return ResponseEntity.ok(TokenValidateResponse.invalid("Token không hợp lệ"));
            }
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Long userId = customUserDetails.getUserId();

            return ResponseEntity.ok(TokenValidateResponse.valid(userId, username));

        } catch (Exception e) {
            return ResponseEntity.ok(TokenValidateResponse.invalid("Có lỗi xảy ra với token"));
        }
    }




}
