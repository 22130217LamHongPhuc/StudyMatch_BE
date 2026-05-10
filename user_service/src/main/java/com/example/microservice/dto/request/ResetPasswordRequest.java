package com.example.microservice.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    String token;
    String newPassword;

}
