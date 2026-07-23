package com.example.microservice.controller;

import com.example.microservice.dto.request.AdminActivateRequest;
import com.example.microservice.dto.request.AdminInvitationRequest;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.AdminInvitationResponse;
import com.example.microservice.dto.respone.AdminInvitationVerifyResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.AdminInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class AdminInvitationController {

    @Autowired
    private AdminInvitationService invitationService;

    @PostMapping("/super-admin/admin-invitations")
    public ResponseEntity<ApiResponse<AdminInvitationResponse>> inviteAdmin(@RequestBody AdminInvitationRequest request) {
        AdminInvitationResponse response = invitationService.createInvitation(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Gửi lời mời thành công",
                response
        ));
    }

    @GetMapping("/public/admin-invitations/verify")
    public ResponseEntity<ApiResponse<AdminInvitationVerifyResponse>> verifyInvitation(@RequestParam String token) {
        AdminInvitationVerifyResponse response = invitationService.verifyInvitation(token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Xác thực lời mời thành công",
                response
        ));
    }

    @PostMapping("/public/admin-invitations/activate")
    public ResponseEntity<ApiResponse<String>> activateAdmin(@RequestBody AdminActivateRequest request) {
        invitationService.activateAdmin(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Kích hoạt tài khoản thành công",
                "Tài khoản Admin đã được kích hoạt"
        ));
    }
}
