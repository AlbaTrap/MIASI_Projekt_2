package com.example.springboot_backend.event.domain.model;

import com.example.springboot_backend.event.domain.valueobject.RawEventId;
import java.time.Instant;

public class RawEvent {
    private final RawEventId id;
    private final EventSource source;
    private final String rawTitle;
    private final String rawDescription;
    private final String rawLocation;
    private final String rawDate;
    private final String rawCategory;
    private final Instant fetchedAt;
    private boolean processed;

    public RawEvent(RawEventId id, EventSource source, String rawTitle, String rawDescription,
                    String rawLocation, String rawDate, String rawCategory, Instant fetchedAt, boolean processed) {
        this.id = id;
        this.source = source;
        this.rawTitle = rawTitle;
        this.rawDescription = rawDescription;
        this.rawLocation = rawLocation;
        this.rawDate = rawDate;
        this.rawCategory = rawCategory;
        this.fetchedAt = fetchedAt;
        this.processed = processed;
    }

    public static RawEvent fetched(EventSource source, String title, String description, String location, String date, String category) {
        return new RawEvent(RawEventId.newId(), source, title, description, location, date, category, Instant.now(), false);
    }

    public void markAsProcessed() { processed = true; }

    public RawEventId id() { return id; }
    public EventSource source() { return source; }
    public String rawTitle() { return rawTitle; }
    public String rawDescription() { return rawDescription; }
    public String rawLocation() { return rawLocation; }
    public String rawDate() { return rawDate; }
    public String rawCategory() { return rawCategory; }
    public Instant fetchedAt() { return fetchedAt; }
    public boolean processed() { return processed; }
}
