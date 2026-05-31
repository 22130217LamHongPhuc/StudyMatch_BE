package com.group_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class ZegoCloudTokenService {

    @Value("${app.zego.app-id:}")
    private String appId;

    @Value("${app.zego.server-secret:}")
    private String serverSecret;

    @Value("${app.zego.token-expiration-minutes:120}")
    private long tokenExpirationMinutes;

    public String generateToken(Long userId, String roomId) {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(serverSecret)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ZEGOCLOUD is not configured");
        }

        long expiresAt = Instant.now().plus(tokenExpirationMinutes, ChronoUnit.MINUTES).getEpochSecond();
        String payload = appId + ':' + roomId + ':' + userId + ':' + expiresAt;
        String signature = sign(payload, serverSecret);
        String token = payload + ':' + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawSignature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawSignature);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate ZEGOCLOUD token", e);
        }
    }
}

