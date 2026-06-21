package com.example.springboot_backend.catalog.domain.event;

    import com.example.springboot_backend.shared.event.DomainEvent;
    import java.time.Instant;
import java.util.UUID;

    public record EventAddedEvent(UUID eventId, Instant occurredAt) implements DomainEvent { }
