package com.example.springboot_backend.event.application.service;
import com.example.springboot_backend.event.application.command.SelectEventCommand;
import com.example.springboot_backend.event.application.dto.EventDto;
import com.example.springboot_backend.event.domain.event.EventSelectedEvent;
import com.example.springboot_backend.event.domain.repository.EventRepository;
import com.example.springboot_backend.event.domain.service.EventSelectionPolicy;
import com.example.springboot_backend.event.domain.valueobject.EventId;
import com.example.springboot_backend.event.mapper.EventMapper;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class SelectEventApplicationService {
    private final EventRepository eventRepository;
    private final EventSelectionPolicy selectionPolicy;
    private final DomainEventPublisher publisher;
    public SelectEventApplicationService(EventRepository eventRepository, EventSelectionPolicy selectionPolicy, DomainEventPublisher publisher) {
        this.eventRepository = eventRepository; this.selectionPolicy = selectionPolicy; this.publisher = publisher;
    }
    @Transactional(readOnly = true)
    public EventDto select(SelectEventCommand command) {
        var event = eventRepository.findById(EventId.of(command.eventId())).orElseThrow(() -> new NotFoundException("Wydarzenie nie istnieje"));
        selectionPolicy.check(event);
        publisher.publish(new EventSelectedEvent(command.userId(), event.id().value(), Instant.now()));
        return EventMapper.toDto(event);
    }
}
