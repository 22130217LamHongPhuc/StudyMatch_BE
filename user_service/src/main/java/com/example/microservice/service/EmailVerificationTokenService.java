package com.example.microservice.service;

import com.example.microservice.entity.EmailVerificationToken;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.EmailVerificationTokenRepository;
import com.example.microservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationTokenService {
    @Autowired
    EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Autowired
    UserRepository userRepository;

    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .userId(user.getUserId())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        String link = "http://localhost:8080/api/auth/verify-email?token=" + token;

//        emailService.send(
//                user.getEmail(),
//                "Xác thực tài khoản",
//                "Bấm vào link để xác thực email: " + link
//        );
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token).orElseThrow(
                () -> new AppException("Token không hợp lệ", StatusCode.INVALID_TOKEN)
        );

        if (Boolean.TRUE.equals(verificationToken.getUsed())) {
            throw new AppException("Token đã được sử dụng", StatusCode.TOKEN_USED);
        }

        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AppException("Token đã hết hạn", StatusCode.TOKEN_EXPIRED);
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));

        user.setEmailVerified(true);
        user.setStatus("ACTIVE");

        verificationToken.setUsed(true);

        userRepository.save(user);
        emailVerificationTokenRepository.save(verificationToken);
    }

    public EmailVerificationToken saveVerificationToken(User user) {

        String tokenVerify = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(tokenVerify)
                .userId(user.getId())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();
        return emailVerificationTokenRepository.save(verificationToken);
    }
}
