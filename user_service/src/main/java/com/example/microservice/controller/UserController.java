package com.example.microservice.controller;


import com.example.microservice.dto.request.SessionReminderEmailRequest;
import com.example.microservice.service.JavaMail;
import com.example.microservice.service.UserService;
import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.BasicUserResponse;
import com.example.microservice.enums.StatusCode;
import org.springframework.http.MediaType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
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

}

