package com.example.microservice.services.client;

import com.example.microservice.services.Dto.BasicUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @PostMapping(value = "/api/users/basic-info", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponseWrapper<List<BasicUserResponse>> getBasicUsers(@RequestBody List<Long> userIds);
}

