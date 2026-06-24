package com.example.springboot_backend.catalog.application.port;

import com.example.springboot_backend.catalog.application.dto.EventSnapshot;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import java.util.Optional;
import java.util.UUID;

public interface EventAvailabilityPort {
    boolean isEventAvailable(UUID eventId);
    Optional<EventSnapshot> getEventDetails(UUID eventId);
    Optional<CatalogEventDto> getFavoriteEventDetails(UUID eventId);
}
