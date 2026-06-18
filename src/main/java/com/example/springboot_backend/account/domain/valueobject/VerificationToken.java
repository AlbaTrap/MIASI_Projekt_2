package com.example.springboot_backend.account.domain.valueobject;

import java.time.Instant;

public record VerificationToken(String value, Instant expiresAt) {
    public boolean expired() { return Instant.now().isAfter(expiresAt); }
}
