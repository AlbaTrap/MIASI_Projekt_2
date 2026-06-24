package com.example.springboot_backend.search.application.service;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.port.CatalogEventsForIndexPort;
import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.repository.EventIndexRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class UpdateSearchIndexService {
    private final EventIndexRepository indexRepository;
    private final CatalogEventsForIndexPort catalogClient;
    public UpdateSearchIndexService(EventIndexRepository indexRepository, CatalogEventsForIndexPort catalogClient) {
        this.indexRepository = indexRepository; this.catalogClient = catalogClient;
    }
    @Transactional
    public void addToIndex(UUID eventId) { catalogClient.getEventForIndex(eventId).ifPresent(dto -> { if ("PUBLISHED".equals(dto.status().name())) indexRepository.saveView(toView(dto)); else indexRepository.remove(eventId); }); }
    @Transactional
    public void updateInIndex(UUID eventId) { addToIndex(eventId); }
    @Transactional
    public void removeFromIndex(UUID eventId) { indexRepository.remove(eventId); }
    @Transactional
    public int rebuildIndex() {
        indexRepository.clear();
        var events = catalogClient.getEventsForIndexing();
        events.forEach(e -> indexRepository.saveView(toView(e)));
        return events.size();
    }
    private SearchEventView toView(CatalogEventDto e) {
        String description = e.description() == null ? "" : (e.description().length() > 180 ? e.description().substring(0, 180) + "..." : e.description());
        return new SearchEventView(e.id(), e.title(), description, e.startDate(), e.city(), locationText(e), e.category().name(), e.status().name());
    }
    private String locationText(CatalogEventDto e) {
        return e.placeName() == null || e.placeName().isBlank() ? (e.address() == null ? "" : e.address()) : e.placeName();
    }
}
