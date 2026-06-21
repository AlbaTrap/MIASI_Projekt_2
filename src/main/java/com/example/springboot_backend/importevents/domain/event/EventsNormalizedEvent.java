package com.example.springboot_backend.importevents.domain.event;

import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;

public record EventsNormalizedEvent(int acceptedEventsCount, int rejectedEventsCount, Instant occurredAt) implements DomainEvent { }
