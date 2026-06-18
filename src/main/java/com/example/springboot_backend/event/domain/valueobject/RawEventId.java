package com.example.springboot_backend.event.domain.valueobject;
import java.util.UUID;
public record RawEventId(UUID value) {
    public static RawEventId newId() { return new RawEventId(UUID.randomUUID()); }
    public static RawEventId of(UUID value) { return new RawEventId(value); }
}
