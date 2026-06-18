package com.example.springboot_backend.notification.domain.event;
import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;
public record NotificationCreatedEvent(UUID notificationId, UUID userId, UUID eventId, String channel, Instant occurredAt) implements DomainEvent { }
