package com.example.springboot_backend.account.domain.valueobject;

import java.util.UUID;

public record UserSessionId(UUID value) {
    public static UserSessionId newId() { return new UserSessionId(UUID.randomUUID()); }
    public static UserSessionId of(UUID value) { return new UserSessionId(value); }
}
