package com.example.springboot_backend.catalog.domain.valueobject;

public record Organizer(String name, String website) {
    public Organizer {
        name = name == null || name.isBlank() ? "Nieznany organizator" : name.trim();
        website = website == null ? "" : website.trim();
    }
}
