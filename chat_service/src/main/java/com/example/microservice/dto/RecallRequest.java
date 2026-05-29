package com.example.microservice.dto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
@Data

public class RecallRequest {
    Long conversationID;
    Long messageID;
}
