package com.example.microservice.services.Dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFriendRequestStatusRequest {
    @NotBlank(message = "status không được null")
    private String status;
}
