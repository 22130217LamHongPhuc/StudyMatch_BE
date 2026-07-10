package com.example.microservice.service;

import com.example.microservice.dto.CreateMatchingItemRequest;
import com.example.microservice.dto.DecidedMatchingItemsDto;
import com.example.microservice.dto.MatchingItemResponse;
import com.example.microservice.dto.UpdateMatchingItemStatusRequest;

public interface MatchingItemService {

    MatchingItemResponse recordAction(CreateMatchingItemRequest request);

    MatchingItemResponse recordActionSkipped(CreateMatchingItemRequest request);

    MatchingItemResponse recordFriendRequest(CreateMatchingItemRequest request);

    MatchingItemResponse recordActionCancelled(CreateMatchingItemRequest request);

    MatchingItemResponse updateMatchingItemStatus(UpdateMatchingItemStatusRequest request);

    DecidedMatchingItemsDto getDecidedMatchingItems(Long userId, Integer page, Integer size);
}
