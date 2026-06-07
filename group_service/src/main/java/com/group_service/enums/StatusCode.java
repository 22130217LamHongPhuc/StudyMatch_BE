package com.group_service.enums;

import org.springframework.http.HttpStatusCode;

public enum StatusCode {
    INVALID_TOKEN ,
    INVALID_REFRESH_TOKEN,
    USER_NOT_FOUND,
    INTERNAL_SERVER_ERROR,
    UNAUTHORIZED,
    ACCESS_DENIED,
    NOT_FOUND,
    EMAIL_ALREADY_IN_USE, INVALID_FILE, PASSWORD_INCORRECT, SUCCESS,
     SESSION_NOT_FOUND, PARTICIPANT_NOT_FOUND;
}

