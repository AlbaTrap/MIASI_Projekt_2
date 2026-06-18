package com.example.springboot_backend.account.domain.valueobject;

import java.util.UUID;

public record UserAccountId(UUID value) {
    public static UserAccountId newId() { return new UserAccountId(UUID.randomUUID()); }
    public static UserAccountId of(UUID value) { return new UserAccountId(value); }
}
