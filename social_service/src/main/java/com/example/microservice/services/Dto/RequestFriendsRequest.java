package com.example.microservice.services.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
@Data
public class RequestFriendsRequest {
    @NotNull(message = "sender id không được null")
    Long sender_id;
    @NotNull(message = "receiver id không được null")
    Long receiver_id;

}
