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
    @PostMapping("/login")
    public  ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
            LoginResponse response = userService.login(request.getEmail(), request.getPassword());
            APIResponse<LoginResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
            return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "tai123");
        return ResponseEntity.ok(map );
    }

}
