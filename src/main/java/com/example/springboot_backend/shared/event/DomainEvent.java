package com.example.springboot_backend.shared.event;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
    default String eventName() { return getClass().getSimpleName(); }
}
