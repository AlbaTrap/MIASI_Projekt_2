package com.example.springboot_backend.search.application.service;

import com.example.springboot_backend.catalog.application.dto.CatalogEventDto;
import com.example.springboot_backend.catalog.application.port.CatalogEventsForIndexPort;
import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.repository.EventIndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSearchIndexServiceTest {

    @Mock
    private EventIndexRepository indexRepository;

    @Mock
    private CatalogEventsForIndexPort catalogClient;

    @InjectMocks
    private UpdateSearchIndexService service;

    @Captor
    private ArgumentCaptor<SearchEventView> viewCaptor;

    private UUID eventId;
    private CatalogEventDto mockPublishedDto;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();

        // Przygotowanie mocka dla opublikowanego wydarzenia (częste użycie w testach)
        mockPublishedDto = mock(CatalogEventDto.class, RETURNS_DEEP_STUBS);
        when(mockPublishedDto.id()).thenReturn(eventId);
        when(mockPublishedDto.title()).thenReturn("Koncert Jazzowy");
        when(mockPublishedDto.description()).thenReturn("Krótki opis");
        when(mockPublishedDto.startDate()).thenReturn(Instant.now());
        when(mockPublishedDto.city()).thenReturn("Wrocław");
        when(mockPublishedDto.placeName()).thenReturn("Stary Klasztor");
        when(mockPublishedDto.category().name()).thenReturn("MUZYKA");
        when(mockPublishedDto.status().name()).thenReturn("PUBLISHED");
    }

    @Test
    void addToIndex() {
        // =========================================================================
        // SCENARIUSZ 1: Wydarzenie ma status PUBLISHED -> Powinno zapisać widok w indeksie
        // =========================================================================
        when(catalogClient.getEventForIndex(eventId)).thenReturn(Optional.of(mockPublishedDto));

        service.addToIndex(eventId);

        verify(indexRepository).saveView(any(SearchEventView.class));
        verify(indexRepository, never()).remove(any());

        // =========================================================================
        // SCENARIUSZ 2: Wydarzenie ma inny status (np. DRAFT) -> Powinno usunąć z indeksu
        // =========================================================================
        reset(indexRepository, catalogClient);
        CatalogEventDto mockDraftDto = mock(CatalogEventDto.class, RETURNS_DEEP_STUBS);
        when(mockDraftDto.status().name()).thenReturn("DRAFT");
        when(catalogClient.getEventForIndex(eventId)).thenReturn(Optional.of(mockDraftDto));

        service.addToIndex(eventId);

        verify(indexRepository).remove(eventId);
        verify(indexRepository, never()).saveView(any());

        // =========================================================================
        // SCENARIUSZ 3: Wydarzenie nie istnieje w katalogu -> Nic nie powinno się wydarzyć
        // =========================================================================
        reset(indexRepository, catalogClient);
        when(catalogClient.getEventForIndex(eventId)).thenReturn(Optional.empty());

        service.addToIndex(eventId);

        verifyNoInteractions(indexRepository);
    }

    @Test
    void updateInIndex() {
        // Metoda wewnętrznie wywołuje addToIndex, więc sprawdzamy poprawność delegacji
        when(catalogClient.getEventForIndex(eventId)).thenReturn(Optional.of(mockPublishedDto));

        service.updateInIndex(eventId);

        verify(catalogClient).getEventForIndex(eventId);
        verify(indexRepository).saveView(any(SearchEventView.class));
    }

    @Test
    void removeFromIndex() {
        service.removeFromIndex(eventId);

        verify(indexRepository).remove(eventId);
        verifyNoInteractions(catalogClient);
    }

    @Test
    void rebuildIndex() {
        // given
        when(indexRepository.clear()).thenReturn(true); // Zakładam typ void lub boolean, mock akceptuje dowolny
        when(catalogClient.getEventsForIndexing()).thenReturn(List.of(mockPublishedDto, mockPublishedDto));

        // when
        int result = service.rebuildIndex();

        // then
        assertThat(result).isEqualTo(2);
        verify(indexRepository).clear();
        verify(indexRepository, times(2)).saveView(any(SearchEventView.class));
    }

    @Test
    void shouldCorrectlyMapToViewWithPrivateRules() {
        // =========================================================================
        // TEST LOGIKI PRYWATNEJ: Obcinanie opisu (>180 znaków) oraz tekst lokalizacji
        // =========================================================================
        // Opis mający dokładnie 190 znaków 'a'
        String longDescription = "a".repeat(190);
        when(mockPublishedDto.description()).thenReturn(longDescription);

        // placeName jest puste, więc powinno pobrać address
        when(mockPublishedDto.placeName()).thenReturn("");
        when(mockPublishedDto.address()).thenReturn("Ulica Testowa 5");

        when(catalogClient.getEventForIndex(eventId)).thenReturn(Optional.of(mockPublishedDto));

        // execute
        service.addToIndex(eventId);

        // capture & verify
        verify(indexRepository).saveView(viewCaptor.capture());
        SearchEventView mappedView = viewCaptor.getValue();

        // Oczekujemy pierwszych 180 znaków i wielokropka na końcu (łącznie 183 znaki)
        String expectedDescription = "a".repeat(180) + "...";
        assertThat(mappedView.description()).isEqualTo(expectedDescription);

        // Oczekujemy podstawienia adresu, bo nazwa miejsca była pusta
        assertThat(mappedView.location()).isEqualTo("Ulica Testowa 5");
    }
}
