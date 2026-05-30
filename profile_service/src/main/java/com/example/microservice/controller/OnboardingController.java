package com.example.microservice.controller;

import com.example.microservice.dto.request.OnboardingSubmitRequest;
import com.example.microservice.dto.response.OnboardingSubmitResponse;
import com.example.microservice.dto.response.UserProfileFullResponse;
import com.example.microservice.services.OnboardingService;
import com.example.microservice.services.ProfileLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@CrossOrigin(origins = "*")
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @Autowired
    private ProfileLoadService profileLoadService;

    @PostMapping("/submit")
    public ResponseEntity<OnboardingSubmitResponse> submitOnboarding(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody OnboardingSubmitRequest request) {
        System.out.println("Received onboarding submission for userId: " + userId);

        if (userId == null) {
            userId = 100L;
        }

        OnboardingSubmitResponse response = onboardingService.submitOnboarding(userId, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }



    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileFullResponse> getFullUserProfile(

            @PathVariable Long userId) {
        UserProfileFullResponse response = profileLoadService.loadUserProfile(userId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileFullResponse> getFullUserProfileFromHeader(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId == null) {
            UserProfileFullResponse response = new UserProfileFullResponse("User ID not provided in header", false);
            return ResponseEntity.badRequest().body(response);
        }

        UserProfileFullResponse response = profileLoadService.loadUserProfile(userId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}

