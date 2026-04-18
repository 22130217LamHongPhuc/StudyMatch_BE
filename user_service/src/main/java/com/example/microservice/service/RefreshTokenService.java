package com.example.microservice.service;

import com.example.microservice.entity.RefreshToken;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user){

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(LocalDateTime.now().plusDays(7));
        rt.setRevoked(false);
        return refreshTokenRepository.save(rt);

    }

    public RefreshToken verifyRefreshToken(String token) throws RuntimeException {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new AppException("Invalid refresh token", StatusCode.INVALID_REFRESH_TOKEN)
        );

        if(refreshToken.isRevoked()){
            throw new AppException("Refresh token revoked", StatusCode.INVALID_REFRESH_TOKEN);
        }

        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new AppException("Refresh token expired", StatusCode.INVALID_REFRESH_TOKEN);
        }

        return refreshToken;
    }

    public void revokeRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(
                        () -> new AppException("Invalid refresh token", StatusCode.INVALID_REFRESH_TOKEN)
                );
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
