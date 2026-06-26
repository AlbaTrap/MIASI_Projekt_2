package com.example.springboot_backend.search.application.service;

import com.example.springboot_backend.search.application.dto.SearchResultsListDto;
import com.example.springboot_backend.search.application.query.SearchEventsQuery;
import com.example.springboot_backend.search.domain.model.EventIndex;
import com.example.springboot_backend.search.domain.repository.EventIndexRepository;
import com.example.springboot_backend.search.domain.valueobject.SearchQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchEventsServiceTest {

    @Mock
    private EventIndexRepository repository;

    @InjectMocks
    private SearchEventsService searchEventsService;

    @Captor
    private ArgumentCaptor<SearchQuery> searchQueryCaptor;

    @Test
    void search() {
        // =========================================================================
        // SCENARIUSZ 1: Poprawne stronicowanie i domyślne sortowanie (Pierwsza strona)
        // =========================================================================
        // given
        SearchEventsQuery queryFirstPage = new SearchEventsQuery(
                "wrocław", "KONCERT", Instant.now(), Instant.now().plusSeconds(3600),
                "Rynek", null, "ASC", 0, 2 // Strona 0, rozmiar 2
        );

        // Zakładam istnienie klasy domenowej EventIndex w domenie wyszukiwania
        EventIndex mockEvent1 = mock(EventIndex.class);
        EventIndex mockEvent2 = mock(EventIndex.class);
        EventIndex mockEvent3 = mock(EventIndex.class);
        List<EventIndex> repoResult = List.of(mockEvent1, mockEvent2, mockEvent3); // Łącznie 3 elementy w bazie

        when(repository.search(any(SearchQuery.class))).thenReturn(repoResult);

        // when
        SearchResultsListDto result1 = searchEventsService.search(queryFirstPage);

        // then
        assertThat(result1).isNotNull();
        assertThat(result1.totalElements()).isEqualTo(3);
        assertThat(result1.pageNumber()).isEqualTo(0);
        assertThat(result1.pageSize()).isEqualTo(2);
        assertThat(result1.results()).hasSize(2); // Ponieważ limit wynosi 2, pobierze tylko 2 pierwsze elementy

        // Weryfikacja translacji na domenę (sortowanie domyślne dla null -> DATE, kierunek ASC)
        verify(repository).search(searchQueryCaptor.capture());
        SearchQuery domainQuery1 = searchQueryCaptor.getValue();
        assertThat(domainQuery1.sorting().field().name()).isEqualTo("DATE");
        assertThat(domainQuery1.sorting().direction().name()).isEqualTo("ASC");

        // =========================================================================
        // SCENARIUSZ 2: Druga strona (offset/skip) oraz sortowanie po nazwie malejąco
        // =========================================================================
        // given
        reset(repository);
        SearchEventsQuery querySecondPage = new SearchEventsQuery(
                "jazz", "KULTURA", Instant.now(), Instant.now(),
                "Klub", "NAZWA", "MALEJACO", 1, 2 // Strona 1, rozmiar 2 (offset = 1 * 2 = 2)
        );

        when(repository.search(any(SearchQuery.class))).thenReturn(repoResult);

        // when
        SearchResultsListDto result2 = searchEventsService.search(querySecondPage);

        // then
        assertThat(result2.pageNumber()).isEqualTo(1);
        assertThat(result2.results()).hasSize(1); // Łącznie były 3, pominęło 2 pierwsze, został 1 element

        // Weryfikacja translacji (NAZWA -> NAME, MALEJACO -> DESC)
        verify(repository).search(searchQueryCaptor.capture());
        SearchQuery domainQuery2 = searchQueryCaptor.getValue();
        assertThat(domainQuery2.sorting().field().name()).isEqualTo("NAME");
        assertThat(domainQuery2.sorting().direction().name()).isEqualTo("DESC");

        // =========================================================================
        // SCENARIUSZ 3: Kierunek DESC (tekst "DESC") oraz sortowanie po kategorii
        // =========================================================================
        // given
        reset(repository);
        SearchEventsQuery queryDescCategory = new SearchEventsQuery(
                "rock", "MUZYKA", Instant.now(), Instant.now(),
                "Hala", "KATEGORIA", "DESC", 0, 10
        );

        when(repository.search(any(SearchQuery.class))).thenReturn(repoResult);

        // when
        searchEventsService.search(queryDescCategory);

        // then
        verify(repository).search(searchQueryCaptor.capture());
        SearchQuery domainQuery3 = searchQueryCaptor.getValue();
        assertThat(domainQuery3.sorting().field().name()).isEqualTo("CATEGORY");
        assertThat(domainQuery3.sorting().direction().name()).isEqualTo("DESC");
    }
}
