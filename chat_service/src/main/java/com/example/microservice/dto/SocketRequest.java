package com.example.microservice.dto;

public class SocketRequest<T> {
    String event;
    T data;
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
