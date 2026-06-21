package com.example.springboot_backend.catalog.application.port;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogEventsForIndexPort {
    Optional<CatalogEventDto> getEventForIndex(UUID eventId);
    List<CatalogEventDto> getEventsForIndexing();
}
