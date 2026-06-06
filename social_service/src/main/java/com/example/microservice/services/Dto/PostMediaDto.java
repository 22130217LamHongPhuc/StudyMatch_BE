package com.example.microservice.services.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostMediaDto {
    private Long id;
    private String mediaUrl;
    private String mediaType;
}
