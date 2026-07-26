package com.example.microservice.service;

import com.example.microservice.annotation.AuditLog;
import com.example.microservice.dto.request.AuthRequest;
import com.example.microservice.dto.respone.AuthResponse;
import com.example.microservice.entity.PasswordResetToken;
import com.example.microservice.entity.RefreshToken;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.PasswordResetTokenRepository;
import com.example.microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    PasswordResetTokenRepository resetTokenRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtService jwtService;
    @Autowired
    RefreshTokenService refreshTokenService;

    public AuthResponse login(AuthRequest request) {
        return authenticate(request, false);
    }

    public AuthResponse loginAdmin(AuthRequest request) {
        return authenticate(request, true);
    }

    private AuthResponse authenticate(AuthRequest request, boolean adminOnly) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy người dùng với email: " + request.getEmail(),
                        StatusCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException("Mật khẩu không chính xác", StatusCode.PASSWORD_INCORRECT);
        }

        String userStatus = user.getStatus() == null ? "" : user.getStatus().trim();
        if (!"ACTIVE".equalsIgnoreCase(userStatus)) {
            throw new AppException("Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động bởi quản trị viên",
                    StatusCode.USER_LOCKED);
        }

        if (adminOnly && !isAdmin(user)) {
            throw new AppException("Tài khoản không có quyền quản trị viên", StatusCode.ACCESS_DENIED);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(user));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                token,
                refreshToken.getToken(),
                user.isOnboardingCompleted(),
                user.getUserId(),
                user.isEmailVerified());
    }

    public void resetPassword(String password, String token) {
        PasswordResetToken verificationToken = resetTokenRepository.findByToken(token).orElseThrow(
                () -> new AppException("Token không hợp lệ", StatusCode.INVALID_TOKEN));

        if (verificationToken.isUsed()) {
            throw new AppException("Token đã được sử dụng", StatusCode.TOKEN_USED);
        }

        if (verificationToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException("Token đã hết hạn", StatusCode.TOKEN_EXPIRED);
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng hiện tại", StatusCode.USER_NOT_FOUND));

        String newPasswordHash = passwordEncoder.encode(password);
        user.setPasswordHash(newPasswordHash);

        verificationToken.setUsed(true);

        userRepository.save(user);
    }

    public PasswordResetToken saveForgetPasswordToken(User user) {
        resetTokenRepository.disableAllValidTokens(user.getUserId());
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getUserId())
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        return resetTokenRepository.save(passwordResetToken);
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null
                && ("admin".equalsIgnoreCase(user.getRole()) || "super_admin".equalsIgnoreCase(user.getRole()));
    }

    public PasswordResetToken saveForgetPasswordTokenAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy người dùng với email: " + email,
                        StatusCode.USER_NOT_FOUND
                ));

        if (!"admin".equalsIgnoreCase(user.getRole()) && !"super_admin".equalsIgnoreCase(user.getRole())) {
            throw new AppException("Tài khoản không phải là quản trị viên", StatusCode.ACCESS_DENIED);
        }

        if ("DELETED".equalsIgnoreCase(user.getStatus()) ||
            "LOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new AppException("Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động bởi quản trị viên", StatusCode.USER_LOCKED);
        }

        return saveForgetPasswordToken(user);
    }

    public void resetPasswordAdmin(String password, String token) {
        PasswordResetToken verificationToken = resetTokenRepository.findByToken(token).orElseThrow(
                () -> new AppException("Token không hợp lệ", StatusCode.INVALID_TOKEN)
        );

        if (verificationToken.isUsed()) {
            throw new AppException("Token đã được sử dụng", StatusCode.TOKEN_USED);
        }

        if (verificationToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException("Token đã hết hạn", StatusCode.TOKEN_EXPIRED);
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng hiện tại", StatusCode.USER_NOT_FOUND));

        if (!"admin".equalsIgnoreCase(user.getRole()) && !"super_admin".equalsIgnoreCase(user.getRole())) {
            throw new AppException("Tài khoản không phải là quản trị viên", StatusCode.ACCESS_DENIED);
        }

        String newPasswordHash = passwordEncoder.encode(password);
        user.setPasswordHash(newPasswordHash);

        verificationToken.setUsed(true);

        userRepository.save(user);
        resetTokenRepository.save(verificationToken);
    }

    @AuditLog(action = "CHANGE_ADMIN_PASSWORD", targetType = "ADMIN", targetId = "#userId", details = "'Đổi mật khẩu thành công'")
    public void changePasswordAdmin(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            throw new AppException("Mật khẩu xác nhận không khớp.", StatusCode.PASSWORD_INCORRECT);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));

        if (!"admin".equalsIgnoreCase(user.getRole()) && !"super_admin".equalsIgnoreCase(user.getRole())) {
            throw new AppException("Tài khoản không phải là quản trị viên", StatusCode.ACCESS_DENIED);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new AppException("Mật khẩu cũ không chính xác.", StatusCode.PASSWORD_INCORRECT);
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
