package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReactionDTO {
    Long  messageID;
    String  emoji;
}
