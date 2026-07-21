package com.example.microservice.service;

import com.example.microservice.dto.respone.AdminOverviewResponse;

public interface AdminOverviewService {
    AdminOverviewResponse getAdminOverview(String timePreset, String startDate, String endDate);
}
