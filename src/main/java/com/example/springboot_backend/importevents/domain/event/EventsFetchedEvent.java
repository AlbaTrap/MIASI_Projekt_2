package com.example.springboot_backend.importevents.domain.event;

import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;

public record EventsFetchedEvent(String source, int amount, Instant occurredAt) implements DomainEvent { }
