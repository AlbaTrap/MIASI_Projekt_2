package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.command.*;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.port.CatalogEventImportPort;
import com.example.springboot_backend.catalog.domain.event.*;
import com.example.springboot_backend.catalog.domain.factory.EventFactory;
import com.example.springboot_backend.catalog.domain.model.*;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.service.EventValidator;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import com.example.springboot_backend.catalog.mapper.CatalogEventMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class EventAdditionService implements CatalogEventImportPort {
    private final CatalogEventRepository repository;
    private final EventFactory factory;
    private final EventValidator validator;
    private final DomainEventPublisher publisher;

    public EventAdditionService(CatalogEventRepository repository, EventFactory factory, EventValidator validator, DomainEventPublisher publisher) {
        this.repository = repository;
        this.factory = factory;
        this.validator = validator;
        this.publisher = publisher;
    }

    @Transactional
    public CatalogEventDto addEvent(AddEventCommand c) {
        CatalogEvent event = factory.create(
                new EventName(c.title()), new EventDescription(c.description()), new EventDate(c.startDate(), c.endDate()),
                Location.of(c.city(), c.address()), EventCategory.fromText(c.category()),
                new Organizer(c.organizerName(), c.organizerWebsite()), EventSource.administrator()
        );
        validator.validate(event);
        CatalogEvent saved = repository.save(event);
        publisher.publish(new EventAddedEvent(saved.id().value(), Instant.now()));
        return CatalogEventMapper.toDto(saved);
    }

    @Override
    @Transactional
    public CatalogEventDto addImportedEvent(AddImportedEventCommand c) {
        CatalogEvent event = factory.create(
                new EventName(c.title()), new EventDescription(c.description()), new EventDate(c.startDate(), c.endDate()),
                Location.of(c.city(), c.address()), EventCategory.fromText(c.category()),
                new Organizer(c.organizerName(), ""), EventSource.scraper(c.sourceAddress())
        );
        validator.validate(event);
        if (c.publishAfterImport()) event.publish();
        CatalogEvent saved = repository.save(event);
        publisher.publish(new EventAddedEvent(saved.id().value(), Instant.now()));
        if (saved.isPublished()) publisher.publish(new EventPublishedEvent(saved.id().value(), Instant.now()));
        return CatalogEventMapper.toDto(saved);
    }
}
