package com.example.microservice.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInvitationResponse {
    private Long invitationId;
    private String email;
    private String status;
    private LocalDateTime expiresAt;
}
