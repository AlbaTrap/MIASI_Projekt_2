package com.example.springboot_backend.importevents.application.service;

import com.example.springboot_backend.importevents.domain.event.EventsFetchedEvent;
import com.example.springboot_backend.importevents.domain.port.EventScraper;
import com.example.springboot_backend.importevents.domain.repository.RawEventRepository;
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
class FetchEventsServiceTest {

    @Mock
    private EventScraper scraper;

    @Mock
    private RawEventRepository repository;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private FetchEventsService fetchEventsService;

    @Test
    void fetchFromScraper() {
        Object mockRawEvent1 = mock(Object.class);
        Object mockRawEvent2 = mock(Object.class);
        List<Object> mockRawEvents = List.of(mockRawEvent1, mockRawEvent2);

        when(scraper.scrapeEvents()).thenReturn((List) mockRawEvents);

        // when
        int resultSuccess = fetchEventsService.fetchFromScraper();

        // then
        assertThat(resultSuccess).isEqualTo(2);
        verify(scraper).scrapeEvents();
        verify(repository).saveAll(mockRawEvents);
        verify(publisher).publish(any(EventsFetchedEvent.class));

        // ---------------------------------------------------------------------

        reset(scraper, repository, publisher); // Czyszczenie liczników interakcji z poprzedniego wywołania
        when(scraper.scrapeEvents()).thenReturn(Collections.emptyList());

        // when
        int resultEmpty = fetchEventsService.fetchFromScraper();

        // then
        assertThat(resultEmpty).isEqualTo(0);
        verify(scraper).scrapeEvents();
        verify(repository).saveAll(Collections.emptyList());
        verify(publisher).publish(any(EventsFetchedEvent.class));
    }
}
