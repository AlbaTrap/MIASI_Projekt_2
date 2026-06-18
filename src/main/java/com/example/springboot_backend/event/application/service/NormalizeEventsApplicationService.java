package com.example.springboot_backend.event.application.service;
import com.example.springboot_backend.event.domain.event.EventsNormalizedEvent;
import com.example.springboot_backend.event.domain.model.EventStatus;
import com.example.springboot_backend.event.domain.repository.EventRepository;
import com.example.springboot_backend.event.domain.repository.RawEventRepository;
import com.example.springboot_backend.event.domain.service.EventNormalizationService;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class NormalizeEventsApplicationService {
    private final RawEventRepository rawEventRepository;
    private final EventRepository eventRepository;
    private final EventNormalizationService normalizationService;
    private final DomainEventPublisher publisher;
    public NormalizeEventsApplicationService(RawEventRepository rawEventRepository, EventRepository eventRepository,
                                             EventNormalizationService normalizationService, DomainEventPublisher publisher) {
        this.rawEventRepository = rawEventRepository; this.eventRepository = eventRepository;
        this.normalizationService = normalizationService; this.publisher = publisher;
    }
    @Transactional
    public String normalize() {
        var rawEvents = rawEventRepository.findUnprocessed();
        var events = rawEvents.stream().map(raw -> {
            var event = normalizationService.normalize(raw);
            raw.markAsProcessed();
            rawEventRepository.save(raw);
            return event;
        }).toList();
        eventRepository.saveAll(events);
        int accepted = (int) events.stream().filter(e -> e.status() == EventStatus.AVAILABLE).count();
        int rejected = events.size() - accepted;
        publisher.publish(new EventsNormalizedEvent(accepted, rejected, Instant.now()));
        return "Znormalizowano: " + events.size() + ", zaakceptowano: " + accepted + ", odrzucono: " + rejected;
    }
}
