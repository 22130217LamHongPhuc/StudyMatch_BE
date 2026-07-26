package com.example.microservice.controller;

import com.example.microservice.dto.request.OnboardingSubmitRequest;
import com.example.microservice.dto.response.ApiResponse;
import com.example.microservice.dto.response.TermUpdateStatusResponse;
import com.example.microservice.dto.response.UserProfileFullResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.services.ProfileLoadService;
import com.example.microservice.services.ProfileUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private ProfileUpdateService profileUpdateService;

    @Autowired
    private ProfileLoadService profileLoadService;

    @PutMapping("/update")
    public ResponseEntity<UserProfileFullResponse> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody OnboardingSubmitRequest request
    ) {
        if (userId == null) {
            UserProfileFullResponse response = new UserProfileFullResponse("User ID not provided in header", false);
            return ResponseEntity.badRequest().body(response);
        }

        try{
            UserProfileFullResponse response = profileUpdateService.updateProfile(userId, request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new UserProfileFullResponse("An error occurred while updating the profile: " + e.getMessage(), false));
        }
    }

    @PutMapping("/internal/update-info")
    public ResponseEntity<Void> updateStudentProfileInfo(
            @RequestParam Long userId,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String avatarUrl
    ) {
        profileUpdateService.updateStudentProfileInfo(userId, fullName, avatarUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/term-status")
    public ApiResponse<TermUpdateStatusResponse> getTermUpdateStatus(
            @RequestHeader(value = "X-User-Id", required = false) Long userId
    ) {
        if (userId == null) {
            return new ApiResponse<>(false, StatusCode.NOT_FOUND, "User ID not provided", null);
        }
        TermUpdateStatusResponse status = profileLoadService.getTermUpdateStatus(userId);
        return new ApiResponse<>(true, StatusCode.SUCCESS, "OK", status);
    }
}

