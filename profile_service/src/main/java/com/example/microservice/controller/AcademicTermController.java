package com.example.microservice.controller;


import com.example.microservice.entity.AcademicTerm;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repositories.AcademicTermRepository;
import jakarta.ws.rs.GET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/academic-terms")
public class AcademicTermController {
    @Autowired
    AcademicTermRepository repository;


    @GetMapping("/active")
    public ResponseEntity<Long> getActiveTermId() {

        Optional<AcademicTerm> academicTerm = repository.findByStatus("active");
        return academicTerm.map(term -> ResponseEntity.ok(term.getTermId()))
                .orElseThrow(() -> new AppException( "No active academic term found",StatusCode.NOT_FOUND));

    }
}
