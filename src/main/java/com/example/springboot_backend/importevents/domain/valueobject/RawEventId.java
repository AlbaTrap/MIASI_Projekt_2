package com.example.springboot_backend.importevents.domain.valueobject;

import java.util.UUID;

public record RawEventId(UUID value) {
    public RawEventId { if (value == null) throw new IllegalArgumentException("RawEventId nie może być pusty"); }
    public static RawEventId newId() { return new RawEventId(UUID.randomUUID()); }
    public static RawEventId of(UUID value) { return new RawEventId(value); }
}
