package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.command.*;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.domain.event.*;
import com.example.springboot_backend.catalog.domain.model.*;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.service.EventValidator;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import com.example.springboot_backend.catalog.mapper.CatalogEventMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class EventUpdateService {
    private final CatalogEventRepository repository;
    private final EventValidator validator;
    private final DomainEventPublisher publisher;
    public EventUpdateService(CatalogEventRepository repository, EventValidator validator, DomainEventPublisher publisher) {
        this.repository = repository;
        this.validator = validator;
        this.publisher = publisher;
    }
    @Transactional
    public CatalogEventDto updateEvent(UpdateEventCommand c) {
        CatalogEvent event = repository.findById(CatalogEventId.of(c.eventId())).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
        event.update(new EventName(c.title()), new EventDescription(c.description()), new EventDate(c.startDate(), c.endDate()),
                Location.of(c.city(), c.address()), EventCategory.fromText(c.category()), new Organizer(c.organizerName(), c.organizerWebsite()));
        validator.validate(event);
        CatalogEvent saved = repository.save(event);
        publisher.publish(new EventUpdatedEvent(saved.id().value(), Instant.now()));
        return CatalogEventMapper.toDto(saved);
    }
    @Transactional
    public CatalogEventDto cancelEvent(CancelEventCommand c) {
        CatalogEvent event = repository.findById(CatalogEventId.of(c.eventId())).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
        event.cancel(c.reason());
        CatalogEvent saved = repository.save(event);
        publisher.publish(new EventCancelledEvent(saved.id().value(), c.reason(), Instant.now()));
        return CatalogEventMapper.toDto(saved);
    }
}
