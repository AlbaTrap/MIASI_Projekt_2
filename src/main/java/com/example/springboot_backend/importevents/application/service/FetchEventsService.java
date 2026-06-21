package com.example.springboot_backend.importevents.application.service;

import com.example.springboot_backend.importevents.domain.event.EventsFetchedEvent;
import com.example.springboot_backend.importevents.domain.port.EventScraper;
import com.example.springboot_backend.importevents.domain.repository.RawEventRepository;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class FetchEventsService {
    private final EventScraper scraper;
    private final RawEventRepository repository;
    private final DomainEventPublisher publisher;
    public FetchEventsService(EventScraper scraper, RawEventRepository repository, DomainEventPublisher publisher) {
        this.scraper = scraper; this.repository = repository; this.publisher = publisher;
    }
    @Transactional
    public int fetchFromScraper() {
        var rawEvents = scraper.scrapeEvents();
        repository.saveAll(rawEvents);
        publisher.publish(new EventsFetchedEvent("SCRAPER", rawEvents.size(), Instant.now()));
        return rawEvents.size();
    }
}
