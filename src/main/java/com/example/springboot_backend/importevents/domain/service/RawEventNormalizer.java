package com.example.springboot_backend.importevents.domain.service;

import com.example.springboot_backend.catalog.application.command.AddImportedEventCommand;
import com.example.springboot_backend.importevents.domain.model.RawEvent;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class RawEventNormalizer {
    public AddImportedEventCommand normalize(RawEvent rawEvent) {
        String[] location = splitLocation(rawEvent.rawLocation());
        Instant startDate = parseDate(rawEvent.rawDate());
        return new AddImportedEventCommand(rawEvent.rawTitle(), rawEvent.rawDescription(), location[1], location[0], location[1],
                startDate, null, rawEvent.rawCategory(), "Organizator z importu", rawEvent.source().name(), true);
    }
    private String[] splitLocation(String rawLocation) {
        if (rawLocation == null || rawLocation.isBlank()) return new String[] {"Wrocław", "Brak adresu"};
        String[] parts = rawLocation.split(",", 2);
        if (parts.length == 1) return new String[] {"Wrocław", parts[0].trim()};
        return new String[] {parts[0].trim(), parts[1].trim()};
    }
    private Instant parseDate(String rawDate) {
        try { return Instant.parse(rawDate); } catch (Exception ignored) { return Instant.now().plusSeconds(86400); }
    }
}
