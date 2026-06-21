package com.example.springboot_backend.search.domain.valueobject;

public record Sorting(SortField field, SortDirection direction) {
    public Sorting {
        if (field == null) field = SortField.DATE;
        if (direction == null) direction = SortDirection.ASC;
    }
}
