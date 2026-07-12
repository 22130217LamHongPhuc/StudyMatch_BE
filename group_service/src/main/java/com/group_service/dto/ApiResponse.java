package com.group_service.dto;


import com.group_service.enums.StatusCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@lombok.NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private StatusCode code;



    public ApiResponse(boolean success, StatusCode code, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

}
