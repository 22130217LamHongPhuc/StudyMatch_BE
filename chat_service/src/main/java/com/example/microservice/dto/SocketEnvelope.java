package com.example.microservice.dto;

import lombok.Data;

@Data
public class SocketEnvelope<T> {
    private String event;
    private T data;
}