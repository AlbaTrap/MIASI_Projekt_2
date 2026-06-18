package com.example.springboot_backend.favorite.domain.event;
import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;
public record FavoriteEventRemovedEvent(UUID userId, UUID eventId, Instant occurredAt) implements DomainEvent { }
