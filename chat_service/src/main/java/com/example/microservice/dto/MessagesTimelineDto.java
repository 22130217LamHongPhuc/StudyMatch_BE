package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagesTimelineDto {
    private String date;
    private long groupMessages;
    private long privateMessages;
    private long total;
}
