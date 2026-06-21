package com.example.springboot_backend.search.domain.valueobject;

public record SearchPhrase(String value) {
    public SearchPhrase { value = value == null ? "" : value.trim(); }
    public boolean isBlank() { return value == null || value.isBlank(); }
}
