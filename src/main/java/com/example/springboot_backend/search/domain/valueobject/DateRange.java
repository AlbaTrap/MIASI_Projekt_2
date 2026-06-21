package com.example.springboot_backend.search.domain.valueobject;

import java.time.Instant;

public record DateRange(Instant from, Instant to) {
    public boolean contains(Instant value) {
        if (value == null) return false;
        return (from == null || !value.isBefore(from)) && (to == null || !value.isAfter(to));
    }
}
