package com.example.microservice.controller;


import com.example.microservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/friends/{id}/mutual")
    public ResponseEntity<?> getProfile (@PathVariable Long id, @RequestParam Long targetUserId){
        return ResponseEntity.ok(userService.getProfile(id, targetUserId));
    }

    @GetMapping("/batch")
    public ResponseEntity<?> getUsersByIds(@RequestParam java.util.List<Long> ids){
        return ResponseEntity.ok(userService.getUsersByIds(ids));
    }

}
