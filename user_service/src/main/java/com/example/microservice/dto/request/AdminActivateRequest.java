package com.example.microservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminActivateRequest {
    private String token;
    private String password;
    private String confirmPassword;
    private String fullName;
}
