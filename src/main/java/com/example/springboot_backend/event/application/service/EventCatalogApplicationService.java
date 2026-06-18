package com.example.springboot_backend.event.application.service;
import com.example.springboot_backend.event.application.command.*;
import com.example.springboot_backend.event.application.dto.EventDto;
import com.example.springboot_backend.event.application.dto.EventSnapshot;
import com.example.springboot_backend.event.application.port.EventAvailabilityPort;
import com.example.springboot_backend.event.domain.event.EventCreatedEvent;
import com.example.springboot_backend.event.domain.event.EventDeletedEvent;
import com.example.springboot_backend.event.domain.model.Event;
import com.example.springboot_backend.event.domain.repository.EventRepository;
import com.example.springboot_backend.event.domain.valueobject.*;
import com.example.springboot_backend.event.mapper.EventMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventCatalogApplicationService implements EventAvailabilityPort {
    private final EventRepository eventRepository;
    private final DomainEventPublisher publisher;
    public EventCatalogApplicationService(EventRepository eventRepository, DomainEventPublisher publisher) {
        this.eventRepository = eventRepository; this.publisher = publisher;
    }
    @Transactional
    public EventDto create(CreateEventCommand c) {
        Event event = Event.manual(new EventTitle(c.title()), c.description(), new Location(c.city(), c.address()),
                new EventDate(c.startDate(), c.endDate()), c.category());
        Event saved = eventRepository.save(event);
        publisher.publish(new EventCreatedEvent(saved.id().value(), Instant.now()));
        return EventMapper.toDto(saved);
    }
    @Transactional
    public EventDto update(UpdateEventCommand c) {
        Event event = eventRepository.findById(EventId.of(c.eventId())).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
        event.update(c.title(), c.description(), c.city(), c.address(), c.startDate(), c.endDate(), c.category());
        return EventMapper.toDto(eventRepository.save(event));
    }
    @Transactional
    public void delete(DeleteEventCommand c) {
        eventRepository.delete(EventId.of(c.eventId()));
        publisher.publish(new EventDeletedEvent(c.eventId(), Instant.now()));
    }
    @Transactional(readOnly = true)
    public List<EventDto> available() { return eventRepository.findAvailable().stream().map(EventMapper::toDto).toList(); }
    @Transactional(readOnly = true)
    public EventDto details(UUID id) { return eventRepository.findById(EventId.of(id)).map(EventMapper::toDto).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje")); }
    @Override
    @Transactional(readOnly = true)
    public boolean isEventAvailable(UUID eventId) { return eventRepository.findById(EventId.of(eventId)).map(e -> e.status().name().equals("AVAILABLE")).orElse(false); }
    @Override
    @Transactional(readOnly = true)
    public Optional<EventSnapshot> getEventDetails(UUID eventId) { return eventRepository.findById(EventId.of(eventId)).filter(e -> e.status().name().equals("AVAILABLE")).map(EventMapper::toSnapshot); }
}
