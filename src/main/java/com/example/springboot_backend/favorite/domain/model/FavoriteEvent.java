package com.example.springboot_backend.favorite.domain.model;
import com.example.springboot_backend.favorite.domain.valueobject.FavoriteEventId;
import java.time.Instant;
import java.util.UUID;

public class FavoriteEvent {
    private final FavoriteEventId id;
    private final UUID userId;
    private final UUID eventId;
    private final Instant addedAt;

    public FavoriteEvent(FavoriteEventId id, UUID userId, UUID eventId, Instant addedAt) {
        this.id = id; this.userId = userId; this.eventId = eventId; this.addedAt = addedAt;
    }
    public static FavoriteEvent create(UUID userId, UUID eventId) { return new FavoriteEvent(FavoriteEventId.newId(), userId, eventId, Instant.now()); }
    public FavoriteEventId id() { return id; }
    public UUID userId() { return userId; }
    public UUID eventId() { return eventId; }
    public Instant addedAt() { return addedAt; }
}
