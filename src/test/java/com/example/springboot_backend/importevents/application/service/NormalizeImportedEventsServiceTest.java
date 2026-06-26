package com.example.springboot_backend.importevents.application.service;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.port.CatalogEventImportPort;
import com.example.springboot_backend.importevents.domain.event.EventsNormalizedEvent;
import com.example.springboot_backend.importevents.domain.model.RawEvent;
import com.example.springboot_backend.importevents.domain.repository.RawEventRepository;
import com.example.springboot_backend.importevents.domain.service.RawEventNormalizer;
import com.example.springboot_backend.shared.event.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalizeImportedEventsServiceTest {

    @Mock
    private RawEventRepository rawEventRepository;

    @Mock
    private RawEventNormalizer normalizer;

    @Mock
    private CatalogEventImportPort catalogPort;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private NormalizeImportedEventsService normalizeImportedEventsService;

    @Test
    void normalize() {
        // given
        RawEvent successfulRawEvent = mock(RawEvent.class);
        RawEvent failingRawEvent = mock(RawEvent.class);

        List<RawEvent> mockRawEvents = List.of(successfulRawEvent, failingRawEvent);

        // Mockujemy obiekt przesyłany z normalizatora do portu katalogu (zakładam uniwersalny mock klasy wejściowej)
        Object mockNormalizedCommandOrDto = mock(Object.class);

        when(rawEventRepository.findUnprocessed()).thenReturn(mockRawEvents);

        // Definiujemy zachowanie dla pierwszego (udanego) zdarzenia
        when(normalizer.normalize(successfulRawEvent)).thenReturn(mockNormalizedCommandOrDto);

        // Definiujemy zachowanie dla drugiego (nieudanego) zdarzenia - rzucenie błędu
        when(normalizer.normalize(failingRawEvent)).thenThrow(new RuntimeException("Błąd walidacji danych wejściowych"));

        // when
        String result = normalizeImportedEventsService.normalize();

        // then
        // 1. Weryfikacja efektów dla udanego przetworzenia
        verify(catalogPort).addImportedEvent(mockNormalizedCommandOrDto);
        verify(successfulRawEvent).markAsProcessed();
        verify(rawEventRepository).save(successfulRawEvent);

        // 2. Weryfikacja efektów dla nieudanego przetworzenia (blok catch)
        verify(failingRawEvent).markAsProcessed();
        verify(rawEventRepository).save(failingRawEvent);

        // 3. Weryfikacja końcowych asercji biznesowych oraz publikacji zdarzenia
        verify(publisher).publish(any(EventsNormalizedEvent.class));

        assertThat(result).isEqualTo("Znormalizowano: 2, dodano do katalogu: 1, odrzucono: 1");
    }

    @Test
    void shouldReturnZeroMetricsWhenNoUnprocessedEventsFound() {
        // given
        when(rawEventRepository.findUnprocessed()).thenReturn(Collections.emptyList());

        // when
        String result = normalizeImportedEventsService.normalize();

        // then
        verifyNoInteractions(normalizer, catalogPort);
        verify(rawEventRepository, never()).save(any());
        verify(publisher).publish(any(EventsNormalizedEvent.class));

        assertThat(result).isEqualTo("Znormalizowano: 0, dodano do katalogu: 0, odrzucono: 0");
    }
}
