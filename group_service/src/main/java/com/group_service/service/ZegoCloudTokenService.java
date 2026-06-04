package com.group_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Service
public class ZegoCloudTokenService {

    private static final String VERSION_FLAG = "04";
    private static final int IV_LENGTH = 16;
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Value("${app.zego.app-id:}")
    private long appId;

    @Value("${app.zego.server-secret:}")
    private String serverSecret;

    @Value("${app.zego.token-ttl-seconds:7200}")
    private int tokenTtlSeconds;




    public String generateToken(Long userId, String roomId) {
        return generateVideoToken(String.valueOf(userId), roomId);
    }

    public String generateVideoToken(String userId, String roomId) {
        String payload = "{\"room_id\":\"" + escapeJson(roomId)
                + "\",\"privilege\":{\"1\":1,\"2\":1},\"stream_id_list\":null}";
        return generateToken04(appId, userId, serverSecret, tokenTtlSeconds, payload);
    }

    private String generateToken04(long appId, String userId, String secret, int effectiveTimeInSeconds, String payload) {
        if (appId == 0) {
            throw new IllegalArgumentException("ZEGO appId is required");
        }
        if (userId == null || userId.isBlank() || userId.length() > 64) {
            throw new IllegalArgumentException("ZEGO userId must be 1-64 characters");
        }
        if (secret == null || secret.length() != 32) {
            throw new IllegalArgumentException("ZEGO serverSecret must be 32 characters");
        }
        if (effectiveTimeInSeconds <= 0) {
            throw new IllegalArgumentException("ZEGO token ttl must be greater than 0");
        }

        byte[] ivBytes = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(ivBytes);

        long nowTime = System.currentTimeMillis() / 1000;
        long expireTime = nowTime + effectiveTimeInSeconds;
        int nonce = new Random().nextInt();
        String content = "{\"app_id\":" + appId
                + ",\"user_id\":\"" + escapeJson(userId)
                + "\",\"ctime\":" + nowTime
                + ",\"expire\":" + expireTime
                + ",\"nonce\":" + nonce
                + ",\"payload\":\"" + escapeJson(payload)
                + "\"}";

        try {
            byte[] contentBytes = encrypt(content.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8), ivBytes);
            ByteBuffer buffer = ByteBuffer.wrap(new byte[contentBytes.length + IV_LENGTH + 12]);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putLong(expireTime);
            packBytes(ivBytes, buffer);
            packBytes(contentBytes, buffer);
            return VERSION_FLAG + Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot generate ZEGO token", ex);
        }
    }

    private byte[] encrypt(byte[] content, byte[] secretKey, byte[] ivBytes) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(content);
    }

    private void packBytes(byte[] bytes, ByteBuffer target) {
        target.putShort((short) bytes.length);
        target.put(bytes);
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

