package com.example.springboot_backend.catalog.domain.valueobject;

public record EventSource(SourceType type, String sourceAddress) {
    public EventSource {
        if (type == null) throw new IllegalArgumentException("Typ źródła jest wymagany");
        sourceAddress = sourceAddress == null ? "" : sourceAddress.trim();
    }
    public static EventSource administrator() { return new EventSource(SourceType.ADMINISTRATOR, "manual"); }
    public static EventSource scraper(String address) { return new EventSource(SourceType.SCRAPER, address); }
}
