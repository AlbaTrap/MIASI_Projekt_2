package com.example.springboot_backend.catalog.domain.valueobject;

import java.time.Instant;

public record EventDate(Instant start, Instant end) {
    public EventDate {
        if (start == null) throw new IllegalArgumentException("Data rozpoczęcia wydarzenia jest wymagana");
        if (end != null && end.isBefore(start)) throw new IllegalArgumentException("Data zakończenia nie może być przed datą rozpoczęcia");
    }
    public boolean isFuture() { return start.isAfter(Instant.now()); }
    public boolean isFinished() { return end != null ? end.isBefore(Instant.now()) : start.isBefore(Instant.now()); }
}
