package com.example.springboot_backend.catalog.domain.model;

public enum EventCategory {
    KONCERT, TEATR, SPORT, EDUKACJA, KULTURA, INNE;

    public static EventCategory fromText(String value) {
        if (value == null || value.isBlank()) return INNE;
        String normalized = value.trim().toUpperCase()
                .replace("Ą", "A").replace("Ć", "C").replace("Ę", "E")
                .replace("Ł", "L").replace("Ń", "N").replace("Ó", "O")
                .replace("Ś", "S").replace("Ż", "Z").replace("Ź", "Z");
        return switch (normalized) {
            case "KONCERT", "CONCERT" -> KONCERT;
            case "TEATR", "THEATRE", "THEATER" -> TEATR;
            case "SPORT" -> SPORT;
            case "EDUKACJA", "EDUCATION" -> EDUKACJA;
            case "KULTURA", "CULTURE" -> KULTURA;
            default -> INNE;
        };
    }
}
