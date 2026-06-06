package com.example.microservice.services.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileSocialStatsResponse {
    private Long postCount;
    private Long likeCount;
    private Long commentCount;
    private Long friendCount;
}
