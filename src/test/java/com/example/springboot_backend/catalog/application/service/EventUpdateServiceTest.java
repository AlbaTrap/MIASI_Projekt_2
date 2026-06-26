package com.example.springboot_backend.catalog.application.service;

import com.example.springboot_backend.catalog.application.command.CancelEventCommand;
import com.example.springboot_backend.catalog.application.command.UpdateEventCommand;
import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.domain.model.CatalogEvent;
import com.example.springboot_backend.catalog.domain.repository.CatalogEventRepository;
import com.example.springboot_backend.catalog.domain.service.EventValidator;
import com.example.springboot_backend.catalog.domain.valueobject.CatalogEventId;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventUpdateServiceTest {

    @Mock
    private CatalogEventRepository repository;

    @Mock
    private EventValidator validator;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private EventUpdateService eventUpdateService;

    @Test
    void updateEvent() {
        // given
        UUID eventUuid = UUID.randomUUID();
        UpdateEventCommand command = new UpdateEventCommand(
                eventUuid, "Tytuł", "Opis","Wrocław", "Hala", "Adres", Instant.now(), Instant.now().plusSeconds(3600),
                 "KONCERT", "Org", "http://org.pl"
        );

        CatalogEvent mockEvent = mock(CatalogEvent.class, Mockito.RETURNS_DEEP_STUBS);
        when(mockEvent.id().value()).thenReturn(eventUuid);
        when(repository.findById(CatalogEventId.of(eventUuid))).thenReturn(Optional.of(mockEvent));
        when(repository.save(mockEvent)).thenReturn(mockEvent);

        // when
        CatalogEventDto result = eventUpdateService.updateEvent(command);

        // then
        verify(mockEvent).update(any(), any(), any(), any(), any(), any());
        verify(validator).validate(mockEvent);
        verify(repository).save(mockEvent);
        verify(publisher).publish(any());
        assertThat(result).isNotNull();
    }

    @Test
    void cancelEvent() {
        // given
        UUID eventUuid = UUID.randomUUID();
        CancelEventCommand command = new CancelEventCommand(eventUuid, "Powód");

        CatalogEvent mockEvent = mock(CatalogEvent.class, Mockito.RETURNS_DEEP_STUBS);
        when(mockEvent.id().value()).thenReturn(eventUuid);
        when(repository.findById(CatalogEventId.of(eventUuid))).thenReturn(Optional.of(mockEvent));
        when(repository.save(mockEvent)).thenReturn(mockEvent);

        // when
        CatalogEventDto result = eventUpdateService.cancelEvent(command);

        // then
        verify(mockEvent).cancel("Powód");
        verify(repository).save(mockEvent);
        verify(publisher).publish(any());
        assertThat(result).isNotNull();
    }
}
