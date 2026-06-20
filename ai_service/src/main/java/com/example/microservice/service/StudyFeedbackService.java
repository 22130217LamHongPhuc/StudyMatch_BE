package com.example.microservice.service;


import com.example.microservice.dto.admin.matching.CreateStudyFeedbackRequest;
import com.example.microservice.dto.admin.matching.StudyFeedbackResponse;

public interface StudyFeedbackService {

    StudyFeedbackResponse createFeedback(CreateStudyFeedbackRequest request);
}