package com.example.springboot_backend.event.domain.service;

import com.example.springboot_backend.event.domain.model.*;
import com.example.springboot_backend.event.domain.valueobject.*;
import com.example.springboot_backend.shared.exception.BusinessException;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class EventNormalizationService {
    private final EventLocationPolicy locationPolicy;
    public EventNormalizationService(EventLocationPolicy locationPolicy) { this.locationPolicy = locationPolicy; }

    public Event normalize(RawEvent rawEvent) {
        try {
            String title = normalizeText(rawEvent.rawTitle());
            Location location = parseLocation(rawEvent.rawLocation());
            Instant start = parseDate(rawEvent.rawDate());
            Event event = new Event(EventId.newId(), new EventTitle(title), rawEvent.rawDescription(),
                    location, new EventDate(start, null), normalizeText(rawEvent.rawCategory()), rawEvent.source(),
                    EventStatus.NORMALIZED, null, Instant.now());
            if (!locationPolicy.accepted(location)) event.reject(RejectionReason.OUTSIDE_WROCLAW);
            else event.markAsAvailable();
            return event;
        } catch (RuntimeException ex) {
            Event rejected = new Event(EventId.newId(), new EventTitle(rawEvent.rawTitle() == null ? "Nieznane wydarzenie" : rawEvent.rawTitle()),
                    rawEvent.rawDescription(), new Location("UNKNOWN", rawEvent.rawLocation()),
                    new EventDate(Instant.now(), null), rawEvent.rawCategory(), rawEvent.source(), EventStatus.REJECTED,
                    RejectionReason.INVALID_FORMAT, Instant.now());
            return rejected;
        }
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) throw new BusinessException("Brak wymaganego tekstu");
        return text.trim().replaceAll("\s+", " ");
    }

    private Location parseLocation(String rawLocation) {
        if (rawLocation == null || rawLocation.isBlank()) throw new BusinessException("Brak lokalizacji");
        String[] parts = rawLocation.split(",", 2);
        if (parts.length == 1) return new Location(parts[0].trim(), "");
        return new Location(parts[0].trim(), parts[1].trim());
    }

    private Instant parseDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) throw new BusinessException("Brak daty");
        try { return Instant.parse(rawDate); }
        catch (Exception ignored) {
            LocalDateTime ldt = LocalDateTime.parse(rawDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return ldt.atZone(ZoneId.systemDefault()).toInstant();
        }
    }
}
