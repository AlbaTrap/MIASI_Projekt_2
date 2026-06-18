package com.example.springboot_backend.event.domain.valueobject;
import java.util.UUID;
public record EventId(UUID value) {
    public static EventId newId() { return new EventId(UUID.randomUUID()); }
    public static EventId of(UUID value) { return new EventId(value); }
}
