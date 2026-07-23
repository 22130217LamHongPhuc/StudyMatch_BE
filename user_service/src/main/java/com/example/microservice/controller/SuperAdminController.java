package com.example.microservice.controller;

import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.AdminUserStatusResponse;
import com.example.microservice.dto.respone.UpdateUserStatusRequest;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.UserService;
import com.example.microservice.service.CustomUserDetails;
import com.example.microservice.exception.AppException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/admins")
@CrossOrigin("*")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class SuperAdminController {

    UserService userService;

    @PatchMapping("/{adminId}/status")
    public ResponseEntity<ApiResponse<AdminUserStatusResponse>> updateAdminStatus(
            @PathVariable Long adminId,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new AppException("Chưa đăng nhập", StatusCode.UNAUTHORIZED);
        }
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String callerRole = userDetails.getUser().getRole();
        if (!"super_admin".equalsIgnoreCase(callerRole)) {
            throw new AppException("Không có quyền thực hiện hành động này", StatusCode.ACCESS_DENIED);
        }

        AdminUserStatusResponse response = userService.updateAdminStatus(adminId, request.getStatus());

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Cập nhật trạng thái Admin thành công",
                response
        ));
    }
}
