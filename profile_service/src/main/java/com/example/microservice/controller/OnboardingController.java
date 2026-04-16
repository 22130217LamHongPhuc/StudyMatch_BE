package com.example.microservice.controller;

import com.example.microservice.dto.request.OnboardingSubmitRequest;
import com.example.microservice.dto.response.OnboardingSubmitResponse;
import com.example.microservice.services.OnboardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@CrossOrigin(origins = "*")
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @PostMapping("/submit")
    public ResponseEntity<OnboardingSubmitResponse> submitOnboarding(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody OnboardingSubmitRequest request) {

        if (userId == null) {
            userId = 1L;
        }

        OnboardingSubmitResponse response = onboardingService.submitOnboarding(userId, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}

