package com.example.microservice.service;

import com.example.microservice.dto.CreateMatchingItemRequest;
import com.example.microservice.dto.MatchingItemResponse;
import com.example.microservice.dto.UpdateMatchingItemStatusRequest;

public interface MatchingItemService {

    MatchingItemResponse recordAction(CreateMatchingItemRequest request);

    MatchingItemResponse updateMatchingItemStatus(UpdateMatchingItemStatusRequest request);
}
