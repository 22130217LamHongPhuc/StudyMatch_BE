package com.example.microservice.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
@Component
public class JWTService {
    String key  ="z8Vj2Q9yHsW4tK7rB1mN5cX8aL3pD6uE0qF9gR2oT7sY4vH1kM8nJ5bC2wX7z9A1dL6mF3pQ0rT8";
    public JWTService(){
    }
    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = parseToken(token);
            String tokenUsername = claims.getSubject();
            Date expiration = claims.getExpiration();
            return tokenUsername.equals(username) && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}