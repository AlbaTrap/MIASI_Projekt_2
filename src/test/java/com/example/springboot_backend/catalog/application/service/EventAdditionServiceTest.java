package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.command.AddEventCommand;
import com.example.springboot_backend.catalog.application.command.AddImportedEventCommand;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.domain.event.EventAddedEvent;
import com.example.springboot_backend.catalog.domain.event.EventPublishedEvent;
import com.example.springboot_backend.catalog.domain.factory.EventFactory;
import com.example.springboot_backend.catalog.domain.model.*;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.service.EventValidator;
import com.example.springboot_backend.catalog.domain.valueobject.*;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.springboot_backend.shared.exception.BusinessException;

class EventAdditionServiceTest {

    private CatalogEventRepository repository;
    private EventFactory factory;
    private EventValidator validator;
    private DomainEventPublisher publisher;
    private EventAdditionService service;

    @BeforeEach
    void setUp() {
        repository = mock(CatalogEventRepository.class);
        factory = mock(EventFactory.class);
        validator = mock(EventValidator.class);
        publisher = mock(DomainEventPublisher.class);
        service = new EventAdditionService(repository, factory, validator, publisher);
    }

    @Test
    void shouldAddEvent() {
        // given
        AddEventCommand command = new AddEventCommand(
                "Koncert", "Opis", "Hala Stulecia", "Wrocław", "Wystawowa 1",
                Instant.now(), Instant.now().plusSeconds(3600), "KONCERT",
                "Organizator", "www.org.pl"
        );

        CatalogEvent event = createDefaultEvent();
        when(factory.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(event);
        when(repository.save(any())).thenReturn(event);

        // when
        CatalogEventDto result = service.addEvent(command);

        // then
        assertNotNull(result);
        assertEquals(event.id().value(), result.id());
        verify(validator).validate(event);
        verify(repository).save(event);
        verify(publisher).publish(any(EventAddedEvent.class));
    }

    @Test
    void shouldAddImportedEventWithoutPublishing() {
        // given
        AddImportedEventCommand command = new AddImportedEventCommand(
                "Importowane", "Opis", "Miejsce", "Wrocław", "Adres",
                Instant.now(), Instant.now().plusSeconds(3600), "INNE",
                "Scraper", "http://source.com", false
        );

        CatalogEvent event = createDefaultEvent();
        when(factory.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(event);
        when(repository.save(any())).thenReturn(event);

        // when
        service.addImportedEvent(command);

        // then
        assertFalse(event.isPublished());
        verify(validator).validate(event);
        verify(repository).save(event);
        verify(publisher).publish(any(EventAddedEvent.class));
        verify(publisher, never()).publish(any(EventPublishedEvent.class));
    }

    @Test
    void shouldAddImportedEventWithPublishing() {
        // given
        AddImportedEventCommand command = new AddImportedEventCommand(
                "Importowane", "Opis", "Miejsce", "Wrocław", "Adres",
                Instant.now(), Instant.now().plusSeconds(3600), "INNE",
                "Scraper", "http://source.com", true
        );

        // Musimy użyć prawdziwego obiektu lub szpiega, bo chcemy żeby status się zmienił
        CatalogEvent event = createDefaultEvent();
        // createDefaultEvent tworzy z DRAFT, publish() zmieni na PUBLISHED jeśli dane są poprawne
        // Musimy zapewnić, że isInWroclaw() zwróci true
        
        when(factory.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(event);
        when(repository.save(any())).thenReturn(event);

        // when
        service.addImportedEvent(command);

        // then
        assertTrue(event.isPublished());
        verify(publisher).publish(any(EventAddedEvent.class));
        verify(publisher).publish(any(EventPublishedEvent.class));
    }

    @Test
    void shouldNotAddEventWhenValidationFails() {
        // given
        AddEventCommand command = new AddEventCommand(
                "Koncert", "Opis", "Hala Stulecia", "Wrocław", "Wystawowa 1",
                Instant.now(), Instant.now().plusSeconds(3600), "KONCERT",
                "Organizator", "www.org.pl"
        );

        CatalogEvent event = createDefaultEvent();
        when(factory.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(event);
        doThrow(new BusinessException("Validation error")).when(validator).validate(event);

        // when & then
        assertThrows(BusinessException.class, () -> service.addEvent(command));
        verify(repository, never()).save(any());
        verify(publisher, never()).publish(any());
    }

    private CatalogEvent createDefaultEvent() {
        Instant now = Instant.now();
        return new CatalogEvent(
                CatalogEventId.newId(),
                new EventName("Test"),
                new EventDescription("Desc"),
                new EventDate(now, now.plusSeconds(3600)),
                Location.of("Wrocław", "Miejsce", "Adres"),
                EventCategory.KONCERT,
                new Organizer("Org", "www"),
                EventSource.administrator(),
                EventStatus.DRAFT,
                now, now, null
        );
    }
}