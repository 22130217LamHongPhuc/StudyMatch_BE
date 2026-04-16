package com.example.microservice.feignClient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8085/")
public interface  UserClient {
//    @GetMapping("/users/{id}")
//    User getUser(@PathVariable("id") Long id);
}
