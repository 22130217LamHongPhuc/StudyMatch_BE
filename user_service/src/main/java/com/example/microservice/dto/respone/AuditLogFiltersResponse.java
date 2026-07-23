package com.example.microservice.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogFiltersResponse {
    private List<String> actions;
    private List<String> targetTypes;
}
