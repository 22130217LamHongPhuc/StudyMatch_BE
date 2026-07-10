package com.example.microservice.controller;

import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.dto.respone.PageResponse;
import com.example.microservice.dto.respone.StudentSearchResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/search")
@CrossOrigin("*")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserSearchController {

    UserService userService;

    /**
     * Search active students by keyword (name or email).
     * GET /api/users/search?keyword=bảo&page=0&size=50
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StudentSearchResponse>>> searchStudents(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        PageResponse<StudentSearchResponse> result = userService.searchStudents(page, size, keyword);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Search students successfully",
                result
        ));
    }
}
