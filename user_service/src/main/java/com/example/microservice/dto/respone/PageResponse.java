package com.example.microservice.dto.respone;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;

    private int page;

    private int limit;

    private long totalElements;

    private int totalPages;

    private boolean hasNext;

    private boolean hasPrevious;



}