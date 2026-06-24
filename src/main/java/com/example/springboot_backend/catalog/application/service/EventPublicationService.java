package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.command.EventIdCommand;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.domain.event.*;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.service.EventValidator;
import com.example.springboot_backend.catalog.domain.valueobject.CatalogEventId;
import com.example.springboot_backend.catalog.mapper.CatalogEventMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class EventPublicationService {
    private final CatalogEventRepository repository;
    private final EventValidator validator;
    private final DomainEventPublisher publisher;
    public EventPublicationService(CatalogEventRepository repository, EventValidator validator, DomainEventPublisher publisher) {
        this.repository = repository;
        this.validator = validator;
        this.publisher = publisher;
    }
    @Transactional
    public CatalogEventDto publish(EventIdCommand c) {
        var event = repository.findById(CatalogEventId.of(c.eventId())).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
        validator.validate(event);
        event.publish();
        var saved = repository.save(event);
        publisher.publish(new EventPublishedEvent(saved.id().value(), Instant.now()));
        return CatalogEventMapper.toDto(saved);
    }
    @Transactional
    public CatalogEventDto hide(EventIdCommand c) {
        var event = repository.findById(CatalogEventId.of(c.eventId())).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
        event.hide();
        var saved = repository.save(event);
        publisher.publish(new EventHiddenEvent(saved.id().value(), Instant.now()));
        return CatalogEventMapper.toDto(saved);
    }
    @Transactional
    public CatalogEventDto archive(EventIdCommand c) {
        var event = repository.findById(CatalogEventId.of(c.eventId())).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
        event.archive();
        var saved = repository.save(event);
        publisher.publish(new EventArchivedEvent(saved.id().value(), Instant.now()));
        return CatalogEventMapper.toDto(saved);
    }
    @Transactional
    public void delete(EventIdCommand c) {
        var eventId = CatalogEventId.of(c.eventId());
        if (!repository.existsById(eventId)) throw new NotFoundException("Wydarzenie nie istnieje");
        repository.delete(eventId);
        publisher.publish(new EventDeletedEvent(c.eventId(), Instant.now()));
    }
}
