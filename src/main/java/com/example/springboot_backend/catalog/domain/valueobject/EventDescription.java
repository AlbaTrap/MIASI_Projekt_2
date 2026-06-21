package com.example.springboot_backend.catalog.domain.valueobject;

public record EventDescription(String value) {
    public EventDescription {
        value = value == null ? "" : value.trim();
    }
}
