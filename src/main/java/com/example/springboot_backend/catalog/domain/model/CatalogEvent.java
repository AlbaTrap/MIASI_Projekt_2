package com.example.springboot_backend.catalog.domain.model;

import com.example.springboot_backend.catalog.domain.valueobject.*;
import com.example.springboot_backend.shared.exception.BusinessException;
import java.time.Instant;

public class CatalogEvent {
    private final CatalogEventId id;
    private EventName name;
    private EventDescription description;
    private EventDate date;
    private Location location;
    private EventCategory category;
    private Organizer organizer;
    private EventSource source;
    private EventStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private String cancelReason;

    public CatalogEvent(CatalogEventId id, EventName name, EventDescription description, EventDate date,
                        Location location, EventCategory category, Organizer organizer, EventSource source,
                        EventStatus status, Instant createdAt, Instant updatedAt, String cancelReason) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.category = category;
        this.organizer = organizer;
        this.source = source;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.cancelReason = cancelReason;
    }

    public void changeDescription(EventDescription description) { this.description = description; touch(); }
    public void changeDate(EventDate date) { this.date = date; touch(); }
    public void changeLocation(Location location) { this.location = location; touch(); }
    public void changeCategory(EventCategory category) { this.category = category == null ? EventCategory.INNE : category; touch(); }

    public void update(EventName name, EventDescription description, EventDate date, Location location, EventCategory category, Organizer organizer) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.category = category == null ? EventCategory.INNE : category;
        this.organizer = organizer;
        touch();
    }

    public void publish() {
        if (status == EventStatus.CANCELLED) throw new BusinessException("Wydarzenie anulowane nie powinno być ponownie publikowane bez decyzji administratora");
        if (status == EventStatus.ARCHIVED) throw new BusinessException("Wydarzenie archiwalne nie może zostać opublikowane");
        if (name == null || date == null || location == null) throw new BusinessException("Wydarzenie nie posiada kompletu danych");
        if (!location.isInWroclaw()) throw new BusinessException("Katalog przechowuje tylko wydarzenia we Wrocławiu");
        status = EventStatus.PUBLISHED;
        touch();
    }

    public void hide() { if (status != EventStatus.ARCHIVED) { status = EventStatus.HIDDEN; touch(); } }
    public void cancel(String reason) { status = EventStatus.CANCELLED; cancelReason = reason == null ? "" : reason; touch(); }
    public void archive() { status = EventStatus.ARCHIVED; touch(); }
    public boolean isPublished() { return status == EventStatus.PUBLISHED; }
    public boolean takesPlaceInWroclaw() { return location != null && location.isInWroclaw(); }
    private void touch() { updatedAt = Instant.now(); }

    public CatalogEventId id() { return id; }
    public EventName name() { return name; }
    public EventDescription description() { return description; }
    public EventDate date() { return date; }
    public Location location() { return location; }
    public EventCategory category() { return category; }
    public Organizer organizer() { return organizer; }
    public EventSource source() { return source; }
    public EventStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public String cancelReason() { return cancelReason; }
}
