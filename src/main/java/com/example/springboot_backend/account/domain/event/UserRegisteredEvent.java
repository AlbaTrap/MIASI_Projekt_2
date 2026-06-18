package com.example.springboot_backend.account.domain.event;

import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(UUID accountId, String email, Instant occurredAt) implements DomainEvent { }
