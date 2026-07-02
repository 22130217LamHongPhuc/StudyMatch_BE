package com.example.microservice.controller;


import com.example.microservice.dto.request.UpdateUserProfileRequest;
import com.example.microservice.dto.request.SessionReminderEmailRequest;
import com.example.microservice.service.JavaMail;
import com.example.microservice.service.UserService;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.BasicUserResponse;
import com.example.microservice.dto.respone.ProfileDto;
import com.example.microservice.enums.StatusCode;
import org.springframework.http.MediaType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({"/api/users", "/users"})
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    JavaMail javaMail;

    @PostMapping(value = "/basic-info", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BasicUserResponse>>> getBasicUsers(@RequestBody List<Long> userIds) {
        List<BasicUserResponse> users = userService.getBasicUsers(userIds);
        return ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Get basic users successfully", users));
    }

    @GetMapping("/friends/{id}/mutual")
    public ResponseEntity<ProfileDto> getProfileWithMutualFriends(
            @PathVariable Long id,
            @RequestParam Long targetUserId
    ) {
        return ResponseEntity.ok(userService.getProfile(id, targetUserId));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<ProfileDto> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateUserProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<List<BasicUserResponse>>> getBasicUsersByQuery(@RequestParam List<Long> ids) {
        List<BasicUserResponse> users = userService.getBasicUsers(ids);
        return ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Get basic users successfully", users));
    }

    @PostMapping("/send-session-reminder")
    public ResponseEntity<Void> sendSessionReminder(@RequestBody SessionReminderEmailRequest request) {
        javaMail.sendSessionReminderEmail(
                request.getEmail(),
                request.getFullName(),
                request.getSessionTitle(),
                request.getStartTime(),
                request.getGroupName()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/fullname")
    public ResponseEntity<ApiResponse<String>> getFullName(@PathVariable Long userId) {
        String fullName = userService.getFullName(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, StatusCode.SUCCESS, "Get fullname successfully", fullName));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<java.util.Map<String, Object>> getUserById(@PathVariable Long userId) {
        com.example.microservice.entity.User user = userService.getProfile(userId);
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        if (user != null) {
            response.put("userId", user.getUserId());
            response.put("fullName", user.getFullName());
            response.put("avatarUrl", user.getAvatarUrl());
            response.put("email", user.getEmail());
            response.put("username", user.getFullName());
        }
        return ResponseEntity.ok(response);
    }

}

