package com.example.hhflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageMetadata {

    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}