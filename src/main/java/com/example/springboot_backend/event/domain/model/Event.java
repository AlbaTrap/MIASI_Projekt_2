package com.example.springboot_backend.event.domain.model;

import com.example.springboot_backend.event.domain.valueobject.*;
import com.example.springboot_backend.shared.exception.BusinessException;
import java.time.Instant;

public class Event {
    private final EventId id;
    private EventTitle title;
    private String description;
    private Location location;
    private EventDate date;
    private String category;
    private EventSource source;
    private EventStatus status;
    private RejectionReason rejectionReason;
    private final Instant createdAt;

    public Event(EventId id, EventTitle title, String description, Location location, EventDate date,
                 String category, EventSource source, EventStatus status, RejectionReason rejectionReason, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.category = category;
        this.source = source;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
    }

    public static Event manual(EventTitle title, String description, Location location, EventDate date, String category) {
        Event event = new Event(EventId.newId(), title, description, location, date, category, EventSource.MANUAL, EventStatus.NORMALIZED, null, Instant.now());
        event.markAsAvailable();
        return event;
    }

    public void markAsAvailable() {
        if (!location.inWroclaw()) throw new BusinessException("Dostępne mogą być tylko wydarzenia we Wrocławiu");
        status = EventStatus.AVAILABLE;
        rejectionReason = null;
    }

    public void reject(RejectionReason reason) { status = EventStatus.REJECTED; rejectionReason = reason; }

    public void update(String title, String description, String city, String address, Instant start, Instant end, String category) {
        this.title = new EventTitle(title);
        this.description = description;
        this.location = new Location(city, address);
        this.date = new EventDate(start, end);
        this.category = category;
        markAsAvailable();
    }

    public EventId id() { return id; }
    public EventTitle title() { return title; }
    public String description() { return description; }
    public Location location() { return location; }
    public EventDate date() { return date; }
    public String category() { return category; }
    public EventSource source() { return source; }
    public EventStatus status() { return status; }
    public RejectionReason rejectionReason() { return rejectionReason; }
    public Instant createdAt() { return createdAt; }
}
