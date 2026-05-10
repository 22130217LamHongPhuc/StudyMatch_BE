package com.example.microservice.service;

import com.example.microservice.entity.EmailVerificationToken;
import com.example.microservice.entity.PasswordResetToken;
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


    public void resetPassword(String password, String token){
        PasswordResetToken verificationToken = resetTokenRepository.findByToken(token).orElseThrow(
                () -> new AppException("Token khong hợp lệ", StatusCode.INVALID_TOKEN)
        );

        if (verificationToken.isUsed()) {
            throw new AppException( "Token đã được sử dụng",StatusCode.TOKEN_USED);
        }

        if (verificationToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException( "Token đã hết hạn",StatusCode.TOKEN_EXPIRED);
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new AppException("không tìm thấy user hiện tại", StatusCode.USER_NOT_FOUND));

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
}
