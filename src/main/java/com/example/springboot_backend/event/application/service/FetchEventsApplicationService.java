package com.example.springboot_backend.event.application.service;
import com.example.springboot_backend.event.domain.event.EventsFetchedEvent;
import com.example.springboot_backend.event.domain.port.EventScraper;
import com.example.springboot_backend.event.domain.repository.RawEventRepository;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class FetchEventsApplicationService {
    private final EventScraper scraper;
    private final RawEventRepository rawEventRepository;
    private final DomainEventPublisher publisher;
    public FetchEventsApplicationService(EventScraper scraper, RawEventRepository rawEventRepository, DomainEventPublisher publisher) {
        this.scraper = scraper; this.rawEventRepository = rawEventRepository; this.publisher = publisher;
    }
    @Transactional
    public int fetchFromScraper() {
        var rawEvents = scraper.scrapeEvents();
        rawEventRepository.saveAll(rawEvents);
        publisher.publish(new EventsFetchedEvent("SCRAPER", rawEvents.size(), Instant.now()));
        return rawEvents.size();
    }
}
