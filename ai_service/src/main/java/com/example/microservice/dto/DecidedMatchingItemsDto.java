package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecidedMatchingItemsDto {

    private List<MatchingItemResponse> accepted;

    private List<MatchingItemResponse> rejected;

    private List<MatchingItemResponse> skipped;
}
