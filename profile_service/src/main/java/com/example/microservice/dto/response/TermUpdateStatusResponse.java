package com.example.microservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TermUpdateStatusResponse {
    private boolean needsUpdate;
    private Long activeTermId;
    private String activeTermName;
    private Long lastUpdatedTermId;
    private String lastUpdatedTermName;
}
