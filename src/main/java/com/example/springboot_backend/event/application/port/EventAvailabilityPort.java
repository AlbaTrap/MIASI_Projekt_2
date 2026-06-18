package com.example.springboot_backend.event.application.port;
import com.example.springboot_backend.event.application.dto.EventSnapshot;
import java.util.Optional;
import java.util.UUID;
public interface EventAvailabilityPort {
    boolean isEventAvailable(UUID eventId);
    Optional<EventSnapshot> getEventDetails(UUID eventId);
}
