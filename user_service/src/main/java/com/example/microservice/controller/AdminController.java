package com.example.microservice.controller;

import com.example.microservice.entity.User;
import com.example.microservice.service.UserService;
import com.example.microservice.service.AuthService;
import com.example.microservice.dto.request.UpdateUserProfileRequest;
import com.example.microservice.dto.request.ChangePasswordRequest;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.service.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminController {

    UserService userService;
    AuthService authService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        User user = userService.getProfile(userId);
        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("userId", user.getUserId());
            response.put("fullName", user.getFullName());
            response.put("avatarUrl", user.getAvatarUrl());
            response.put("email", user.getEmail());
            response.put("bio", user.getBio());
            response.put("username", user.getFullName());
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAdminProfile(
            @RequestBody UpdateUserProfileRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        Long userId = userDetails.getUser().getUserId();

        Map<String, Object> response = userService.updateAdminProfile(userId, request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Cập nhật thông tin admin thành công",
                response
        ));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePasswordAdmin(
            @RequestBody ChangePasswordRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        Long userId = userDetails.getUser().getUserId();

        authService.changePasswordAdmin(
                userId,
                request.getOldPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Đổi mật khẩu thành công",
                null
        ));
    }
}
