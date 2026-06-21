package com.example.springboot_backend.catalog.domain.factory;

import com.example.springboot_backend.catalog.domain.model.*;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class EventFactory {
    public CatalogEvent create(EventName name, EventDescription description, EventDate date, Location location,
                               EventCategory category, Organizer organizer, EventSource source) {
        Instant now = Instant.now();
        return new CatalogEvent(CatalogEventId.newId(), name, description, date, location,
                category == null ? EventCategory.INNE : category, organizer, source, EventStatus.DRAFT, now, now, null);
    }
}
