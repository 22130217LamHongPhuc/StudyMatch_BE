package com.example.microservice.controller;


import com.example.microservice.dto.respone.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Admin endpoint is working", "Hello Admin"));
    }
}
