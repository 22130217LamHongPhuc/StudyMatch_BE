package com.group_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicUserResponse {

    @JsonAlias("user_id")
    private Long userId;

    @JsonAlias("full_name")
    private String fullName;


    public String getUserName() {
        return fullName;
    }
}