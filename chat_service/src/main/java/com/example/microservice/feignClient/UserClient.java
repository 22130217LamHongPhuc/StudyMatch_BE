package com.example.microservice.feignClient;


import com.example.microservice.dto.TokenValidateResponse;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "http://localhost:8085/")
public interface  UserClient {
//    @GetMapping("/users/{id}")
//    User getUser(@PathVariable("id") Long id);

    @GetMapping("/api/auth/validate-token")
    TokenValidateResponse validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );
}
