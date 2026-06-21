package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.dto.EventSnapshot;
import com.example.springboot_backend.catalog.application.port.CatalogEventsForIndexPort;
import com.example.springboot_backend.catalog.application.port.EventAvailabilityPort;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.valueobject.CatalogEventId;
import com.example.springboot_backend.catalog.mapper.CatalogEventMapper;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventQueryService implements EventAvailabilityPort, CatalogEventsForIndexPort {
    private final CatalogEventRepository repository;
    public EventQueryService(CatalogEventRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public CatalogEventDto getEvent(UUID id) {
        return repository.findById(CatalogEventId.of(id)).map(CatalogEventMapper::toDto).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
    }
    @Transactional(readOnly = true)
    public boolean eventExists(UUID id) { return repository.existsById(CatalogEventId.of(id)); }
    @Transactional(readOnly = true)
    public String getEventStatus(UUID id) { return getEvent(id).status().name(); }
    @Transactional(readOnly = true)
    public List<CatalogEventDto> getPublishedEvents() { return repository.findPublished().stream().map(CatalogEventMapper::toDto).toList(); }
    @Override
    @Transactional(readOnly = true)
    public boolean isEventAvailable(UUID eventId) {
        return repository.findById(CatalogEventId.of(eventId)).map(e -> e.status() == EventStatus.PUBLISHED).orElse(false);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<EventSnapshot> getEventDetails(UUID eventId) {
        return repository.findById(CatalogEventId.of(eventId)).filter(e -> e.status() == EventStatus.PUBLISHED).map(CatalogEventMapper::toSnapshot);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<CatalogEventDto> getEventForIndex(UUID eventId) {
        return repository.findById(CatalogEventId.of(eventId)).map(CatalogEventMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CatalogEventDto> getEventsForIndexing() {
        return repository.findForIndexing().stream().map(CatalogEventMapper::toDto).toList();
    }
}
