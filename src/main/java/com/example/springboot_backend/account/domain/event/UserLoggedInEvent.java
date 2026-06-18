package com.example.springboot_backend.account.domain.event;

import com.example.springboot_backend.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record UserLoggedInEvent(UUID accountId, UUID sessionId, Instant occurredAt) implements DomainEvent { }
