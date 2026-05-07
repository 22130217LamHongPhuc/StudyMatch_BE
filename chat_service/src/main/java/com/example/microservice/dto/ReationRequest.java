package com.example.microservice.dto;


import lombok.Data;

@Data
public class ReationRequest {
    int messageID;
    String emoji;
    int currentUser;
}
