package com.example.springboot_backend.search.domain.valueobject;

public record Pagination(int pageNumber, int pageSize) {
    public Pagination {
        if (pageNumber < 0) pageNumber = 0;
        if (pageSize <= 0) pageSize = 20;
        if (pageSize > 100) pageSize = 100;
    }
    public int offset() { return pageNumber * pageSize; }
}
