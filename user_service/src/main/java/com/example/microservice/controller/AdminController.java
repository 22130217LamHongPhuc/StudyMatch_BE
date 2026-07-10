package com.example.microservice.controller;

import com.example.microservice.entity.User;
import com.example.microservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminController {

    UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        User user = userService.getProfile(userId);
        Map<String, Object> response = new HashMap<>();
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
