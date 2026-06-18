package com.example.springboot_backend.event.domain.event;
import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;
public record EventCreatedEvent(UUID eventId, Instant occurredAt) implements DomainEvent { }
