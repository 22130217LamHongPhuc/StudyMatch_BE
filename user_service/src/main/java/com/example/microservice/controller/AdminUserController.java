package com.example.microservice.controller;

import com.example.microservice.dto.respone.*;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.UserService;
import com.example.microservice.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin("*")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminUserController {

        UserService userService;

        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<AdminUserListItemResponse>>> getUsersForAdmin(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int limit,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String role) {

                String resolvedRole = role;
                Long callerUserId = null;
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                        Object principal = authentication.getPrincipal();
                        if (principal instanceof CustomUserDetails) {
                                CustomUserDetails userDetails = (CustomUserDetails) principal;
                                String callerRole = userDetails.getUser().getRole();
                                if ("admin".equalsIgnoreCase(callerRole)) {
                                        resolvedRole = "student";
                                }
                                callerUserId = userDetails.getUser().getUserId();
                        }
                }

                PageResponse<AdminUserListItemResponse> response = userService.getUsersForAdmin(
                                page,
                                limit,
                                keyword,
                                status,
                                resolvedRole,
                                callerUserId);
                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Lấy danh sách người dùng thành công",
                                response));
        }

        @PatchMapping("/{userId}/status")
        public ResponseEntity<ApiResponse<AdminUserStatusResponse>> updateUserStatusForAdmin(
                        @PathVariable Long userId,
                        @Valid @RequestBody UpdateUserStatusRequest request) {
                AdminUserStatusResponse response = userService.updateStatusUser(
                                userId,
                                request.getStatus());

                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Cập nhật trạng thái người dùng thành công",
                                response));
        }
}
