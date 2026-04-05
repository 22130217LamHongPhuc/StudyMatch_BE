package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.dto.LoginRequest;
import com.example.microservice.dto.LoginResponse;
import com.example.microservice.dto.RegisterRequest;
import com.example.microservice.dto.RegisterResponse;
import com.example.microservice.handler.ResponseStatus;
import com.example.microservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = userService.registerUser(request);
            APIResponse<RegisterResponse> apiResponse = new APIResponse<>(ResponseStatus.CREATED, response);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (IllegalArgumentException e) {
            String data = "Invalid username or password";
            System.out.println("lỗi"+ e.getMessage());
            APIResponse<RegisterResponse> apiResponse = new APIResponse<>(ResponseStatus.BAD_REQUEST, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        } catch (Exception e) {
            System.out.println("lỗi"+ e.getMessage());
            APIResponse<RegisterResponse> apiResponse = new APIResponse<>(ResponseStatus.INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("/login")
    public  ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
            LoginResponse response = userService.login(request.getEmail(), request.getPassword());
            APIResponse<LoginResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
            return ResponseEntity.ok(apiResponse);
    }
}
