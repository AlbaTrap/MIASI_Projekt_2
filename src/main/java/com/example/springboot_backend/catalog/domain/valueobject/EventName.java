package com.example.springboot_backend.catalog.domain.valueobject;

public record EventName(String value) {
    public EventName {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Nazwa wydarzenia jest wymagana");
        value = value.trim();
    }
}
