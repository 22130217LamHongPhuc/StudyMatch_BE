package com.example.microservice.service;

import com.example.microservice.annotation.AuditLog;
import com.example.microservice.dto.request.AdminActivateRequest;
import com.example.microservice.dto.request.AdminInvitationRequest;
import com.example.microservice.dto.respone.AdminInvitationResponse;
import com.example.microservice.dto.respone.AdminInvitationVerifyResponse;
import com.example.microservice.entity.AdminInvitation;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.AdminInvitationRepository;
import com.example.microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminInvitationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminInvitationRepository invitationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    @AuditLog(action = "INVITE_ADMIN", targetType = "ADMIN_INVITATION", targetId = "#request.email", details = "'Gửi lời mời tham gia quản trị đến email: ' + #request.email")
    public AdminInvitationResponse createInvitation(AdminInvitationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String callerRole = userDetails.getUser().getRole();
        if (!"super_admin".equalsIgnoreCase(callerRole)) {
            throw new AppException("Không có quyền thực hiện hành động này", StatusCode.ACCESS_DENIED);
        }

        String email = request.getEmail();

        if (userRepository.existsByEmail(email)) {
            User existingUser = userRepository.findByEmail(email).get();
            if (!"admin".equalsIgnoreCase(existingUser.getRole())) {
                throw new AppException("Email đã được sử dụng cho tài khoản sinh viên.", StatusCode.EMAIL_ALREADY_USED_BY_STUDENT);
            }
            if ("ACTIVE".equalsIgnoreCase(existingUser.getStatus())) {
                throw new AppException("Tài khoản admin này đã được kích hoạt.", StatusCode.EMAIL_ALREADY_IN_USE);
            }
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName("Admin");
            newUser.setPasswordHash("PENDING");
            newUser.setRole("admin");
            newUser.setStatus("PENDING_ACTIVATION");
            newUser.setEmailVerified(false);
            newUser.setOnboardingCompleted(false);
            userRepository.save(newUser);
        }

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        AdminInvitation invitation = invitationRepository.findByEmail(email).orElse(null);
        if (invitation == null) {
            invitation = new AdminInvitation();
            invitation.setEmail(email);
        }
        invitation.setFullName("Admin");
        invitation.setTokenHash(tokenHash);
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusHours(24));
        invitationRepository.save(invitation);

        String link = frontendUrl + "/activate-admin?token=" + rawToken;
        mailService.sendMailTo(email, "Lời mời tham gia ban quản trị StudyMatch", "admin-invitation", link);

        return AdminInvitationResponse.builder()
                .invitationId(invitation.getId())
                .email(invitation.getEmail())
                .status(invitation.getStatus())
                .expiresAt(invitation.getExpiresAt())
                .build();
    }

    public AdminInvitationVerifyResponse verifyInvitation(String rawToken) {
        String tokenHash = hashToken(rawToken);
        AdminInvitation invitation = invitationRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AppException("Lời mời không hợp lệ hoặc không tồn tại.", StatusCode.INVITATION_NOT_FOUND));

        if (!"PENDING".equalsIgnoreCase(invitation.getStatus())) {
            throw new AppException("Lời mời đã được sử dụng hoặc đã hủy.", StatusCode.INVITATION_ALREADY_USED);
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus("EXPIRED");
            invitationRepository.save(invitation);
            throw new AppException("Lời mời đã hết hạn.", StatusCode.INVITATION_EXPIRED);
        }

        return AdminInvitationVerifyResponse.builder()
                .email(invitation.getEmail())
                .fullName(invitation.getFullName())
                .build();
    }

    @Transactional
    public void activateAdmin(AdminActivateRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new AppException("Họ và tên không được để trống.", StatusCode.ACCESS_DENIED);
        }
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Mật khẩu xác nhận không khớp.", StatusCode.PASSWORD_INCORRECT);
        }

        String tokenHash = hashToken(request.getToken());
        AdminInvitation invitation = invitationRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AppException("Lời mời không hợp lệ hoặc không tồn tại.", StatusCode.INVITATION_NOT_FOUND));

        if (!"PENDING".equalsIgnoreCase(invitation.getStatus())) {
            throw new AppException("Lời mời đã được sử dụng hoặc đã hủy.", StatusCode.INVITATION_ALREADY_USED);
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus("EXPIRED");
            invitationRepository.save(invitation);
            throw new AppException("Lời mời đã hết hạn.", StatusCode.INVITATION_EXPIRED);
        }

        User user = userRepository.findByEmail(invitation.getEmail())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng.", StatusCode.USER_NOT_FOUND));

        user.setFullName(request.getFullName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus("ACTIVE");
        user.setEmailVerified(true);
        userRepository.save(user);

        invitation.setFullName(request.getFullName().trim());
        invitation.setStatus("ACCEPTED");
        invitationRepository.save(invitation);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
