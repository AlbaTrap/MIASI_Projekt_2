package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.command.EventIdCommand;
import com.example.springboot_backend.catalog.domain.event.EventArchivedEvent;
import com.example.springboot_backend.catalog.domain.event.EventDeletedEvent;
import com.example.springboot_backend.catalog.domain.event.EventHiddenEvent;
import com.example.springboot_backend.catalog.domain.event.EventPublishedEvent;
import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.catalog.domain.model.EventCategory;
import com.example.springboot_backend.catalog.domain.model.EventStatus;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.service.EventValidator;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import com.example.springboot_backend.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublicationServiceTest {

    @Mock
    private CatalogEventRepository repository;

    @Mock
    private EventValidator validator;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private EventPublicationService service;

    private UUID eventId;
    private EventIdCommand command;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        command = new EventIdCommand(eventId);
    }

    @Test
    void publish_ShouldPublishEvent_WhenEventExistsAndIsValid() {
        // given
        CatalogEvent event = createEvent(EventStatus.HIDDEN);
        when(repository.findById(CatalogEventId.of(eventId))).thenReturn(Optional.of(event));
        when(repository.save(any())).thenReturn(event);

        // when
        var result = service.publish(command);

        // then
        assertEquals(EventStatus.PUBLISHED, event.status());
        verify(validator).validate(event);
        verify(repository).save(event);
        verify(publisher).publish(any(EventPublishedEvent.class));
        assertNotNull(result);
    }

    @Test
    void publish_ShouldThrowNotFoundException_WhenEventDoesNotExist() {
        // given
        when(repository.findById(CatalogEventId.of(eventId))).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> service.publish(command));
        verify(repository, never()).save(any());
        verify(publisher, never()).publish(any());
    }

    @Test
    void hide_ShouldHideEvent_WhenEventExists() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findById(CatalogEventId.of(eventId))).thenReturn(Optional.of(event));
        when(repository.save(any())).thenReturn(event);

        // when
        var result = service.hide(command);

        // then
        assertEquals(EventStatus.HIDDEN, event.status());
        verify(repository).save(event);
        verify(publisher).publish(any(EventHiddenEvent.class));
        assertNotNull(result);
    }

    @Test
    void hide_ShouldThrowNotFoundException_WhenEventDoesNotExist() {
        // given
        when(repository.findById(CatalogEventId.of(eventId))).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> service.hide(command));
    }

    @Test
    void archive_ShouldArchiveEvent_WhenEventExists() {
        // given
        CatalogEvent event = createEvent(EventStatus.PUBLISHED);
        when(repository.findById(CatalogEventId.of(eventId))).thenReturn(Optional.of(event));
        when(repository.save(any())).thenReturn(event);

        // when
        var result = service.archive(command);

        // then
        assertEquals(EventStatus.ARCHIVED, event.status());
        verify(repository).save(event);
        verify(publisher).publish(any(EventArchivedEvent.class));
        assertNotNull(result);
    }

    @Test
    void archive_ShouldThrowNotFoundException_WhenEventDoesNotExist() {
        // given
        when(repository.findById(CatalogEventId.of(eventId))).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> service.archive(command));
    }

    @Test
    void delete_ShouldDeleteEvent_WhenEventExists() {
        // given
        when(repository.existsById(CatalogEventId.of(eventId))).thenReturn(true);

        // when
        service.delete(command);

        // then
        verify(repository).delete(CatalogEventId.of(eventId));
        verify(publisher).publish(any(EventDeletedEvent.class));
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenEventDoesNotExist() {
        // given
        when(repository.existsById(CatalogEventId.of(eventId))).thenReturn(false);

        // when & then
        assertThrows(NotFoundException.class, () -> service.delete(command));
        verify(repository, never()).delete(any());
        verify(publisher, never()).publish(any());
    }

    private CatalogEvent createEvent(EventStatus status) {
        return new CatalogEvent(
                CatalogEventId.of(eventId),
                new EventName("Test Event"),
                new EventDescription("Description"),
                new EventDate(Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200)),
                new Location("Rynek", new Address("Rynek", "1", "Wrocław"), new Coordinates(51.0, 17.0)),
                EventCategory.INNE,
                new Organizer("Org", "http://org.com"),
                EventSource.administrator(),
                status,
                Instant.now(),
                Instant.now(),
                null
        );
    }
}