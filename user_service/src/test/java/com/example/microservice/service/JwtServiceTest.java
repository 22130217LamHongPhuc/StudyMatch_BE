package com.example.microservice.service;

import com.example.microservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateTokenShouldContainRoleAndUserIdClaims() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "SECRET_KEY",
                "nlu_echo_by_phuc_lam_2004_this_is_be_for_applicati"
        );

        User user = new User();
        user.setUserId(99L);
        user.setEmail("admin@studymatch.com");
        user.setRole("admin");
        user.setPasswordHash("encoded");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        assertEquals("admin@studymatch.com", jwtService.extractUsername(token));
        assertEquals("admin", jwtService.extractRole(token));
        assertEquals(99L, jwtService.extractUserId(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
}
