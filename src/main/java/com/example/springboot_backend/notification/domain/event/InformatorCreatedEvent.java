package com.example.springboot_backend.notification.domain.event;
import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;
public record InformatorCreatedEvent(UUID informatorId, UUID userId, UUID eventId, Instant occurredAt) implements DomainEvent { }
