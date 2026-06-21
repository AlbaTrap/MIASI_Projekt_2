package com.example.springboot_backend.importevents.application.service;

import com.example.springboot_backend.catalog.application.port.CatalogEventImportPort;
import com.example.springboot_backend.importevents.domain.event.EventsNormalizedEvent;
import com.example.springboot_backend.importevents.domain.repository.RawEventRepository;
import com.example.springboot_backend.importevents.domain.service.RawEventNormalizer;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class NormalizeImportedEventsService {
    private final RawEventRepository rawEventRepository;
    private final RawEventNormalizer normalizer;
    private final CatalogEventImportPort catalogPort;
    private final DomainEventPublisher publisher;
    public NormalizeImportedEventsService(RawEventRepository rawEventRepository, RawEventNormalizer normalizer, CatalogEventImportPort catalogPort, DomainEventPublisher publisher) {
        this.rawEventRepository = rawEventRepository; this.normalizer = normalizer; this.catalogPort = catalogPort; this.publisher = publisher;
    }
    public String normalize() {
        var rawEvents = rawEventRepository.findUnprocessed();
        int accepted = 0;
        int rejected = 0;
        for (var raw : rawEvents) {
            try {
                catalogPort.addImportedEvent(normalizer.normalize(raw));
                raw.markAsProcessed();
                rawEventRepository.save(raw);
                accepted++;
            } catch (RuntimeException ex) {
                raw.markAsProcessed();
                rawEventRepository.save(raw);
                rejected++;
            }
        }
        publisher.publish(new EventsNormalizedEvent(accepted, rejected, Instant.now()));
        return "Znormalizowano: " + rawEvents.size() + ", dodano do katalogu: " + accepted + ", odrzucono: " + rejected;
    }
}
