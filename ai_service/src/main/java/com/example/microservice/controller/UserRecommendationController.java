package com.example.microservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class UserRecommendationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/recommend-users")
    public ResponseEntity<Object> getRecommendUsers(
            @RequestParam("user_id") Long userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "3") Integer limit
    ) {
        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8000/api/recommend-users")
                .queryParam("user_id", userId)
                .queryParam("page", page)
                .queryParam("limit", limit)
                .toUriString();
        try {
            Object response = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
