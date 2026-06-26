package com.example.springboot_backend.search.domain.service;

import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.valueobject.DateRange;
import com.example.springboot_backend.search.domain.valueobject.SearchCriteria;
import com.example.springboot_backend.search.domain.valueobject.SearchQuery;
import com.example.springboot_backend.search.domain.valueobject.SearchPhrase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventMatchingServiceTest {

    private final EventMatchingService service = new EventMatchingService();

    private SearchEventView validView;
    private Instant eventDate;

    @BeforeEach
    void setUp() {
        eventDate = Instant.parse("2026-08-15T12:00:00Z");

        // Budujemy domyślny, poprawny widok wydarzenia, który spełnia większość kryteriów
        validView = new SearchEventView(
                UUID.randomUUID(),
                "Koncert Rockowy we Wrocławiu",
                "Świetna muzyka na żywo w centrum miasta",
                eventDate,
                "Wrocław",
                "Rynek",
                "MUZYKA",
                "PUBLISHED"
        );
    }

    @Test
    void matches() {
        // =========================================================================
        // SCENARIUSZ 1: Pełny sukces - wszystkie filtry przekazane i pasują
        // =========================================================================
        SearchQuery perfectQuery = createMockQuery("Rockowy", "MUZYKA", "Rynek", true);

        boolean resultSuccess = service.matches(validView, perfectQuery);
        assertThat(resultSuccess).isTrue();

        // =========================================================================
        // SCENARIUSZ 2: Filtry są puste (blank / null) -> Powinien dopasować (case-insensitive)
        // =========================================================================
        SearchQuery emptyQuery = createMockQuery("   ", "", null, false);

        boolean resultEmptyFilters = service.matches(validView, emptyQuery);
        assertThat(resultEmptyFilters).isTrue();

        // =========================================================================
        // SCENARIUSZ 3: Fraza pasuje do opisu (shortDescription), a nie do tytułu
        // =========================================================================
        SearchQuery phraseInDescriptionQuery = createMockQuery("muzyka", "", "", false);

        boolean resultPhraseInDesc = service.matches(validView, phraseInDescriptionQuery);
        assertThat(resultPhraseInDesc).isTrue();

        // =========================================================================
        // SCENARIUSZ 4: BŁĄD - Fraza nie pasuje nigdzie
        // =========================================================================
        SearchQuery wrongPhraseQuery = createMockQuery("Siatkówka", "", "", false);

        boolean resultWrongPhrase = service.matches(validView, wrongPhraseQuery);
        assertThat(resultWrongPhrase).isFalse();

        // =========================================================================
        // SCENARIUSZ 5: BŁĄD - Kategoria się nie zgadza
        // =========================================================================
        SearchQuery wrongCategoryQuery = createMockQuery("", "SPORT", "", false);

        boolean resultWrongCategory = service.matches(validView, wrongCategoryQuery);
        assertThat(resultWrongCategory).isFalse();

        // =========================================================================
        // SCENARIUSZ 6: BŁĄD - Lokalizacja się nie zgadza
        // =========================================================================
        SearchQuery wrongLocationQuery = createMockQuery("", "", "Hala Stulecia", false);

        boolean resultWrongLocation = service.matches(validView, wrongLocationQuery);
        assertThat(resultWrongLocation).isFalse();

        // =========================================================================
        // SCENARIUSZ 7: BŁĄD - Data wykracza poza zakres DateRange
        // =========================================================================
        SearchQuery wrongDateQuery = createMockQuery("", "", "", true);
        // Nadpisujemy mock zachowania zakresu dat, aby wskazał, że nie zawiera daty wydarzenia
        when(wrongDateQuery.criteria().dateRange().contains(eventDate)).thenReturn(false);

        boolean resultWrongDate = service.matches(validView, wrongDateQuery);
        assertThat(resultWrongDate).isFalse();

        // =========================================================================
        // SCENARIUSZ 8: BŁĄD - Status wydarzenia to nie PUBLISHED (np. DRAFT)
        // =========================================================================
        SearchEventView draftView = new SearchEventView(
                UUID.randomUUID(), "Tytuł", "Opis", eventDate, "Wrocław", "Rynek", "MUZYKA", "DRAFT"
        );
        SearchQuery anyQuery = createMockQuery("", "", "", false);

        boolean resultWrongStatus = service.matches(draftView, anyQuery);
        assertThat(resultWrongStatus).isFalse();
    }

    /**
     * Pomocnicza metoda do szybkiego budowania mocka struktury SearchQuery
     */
    private SearchQuery createMockQuery(String phrase, String category, String location, boolean mockDateRange) {
        SearchQuery query = mock(SearchQuery.class);
        SearchPhrase searchPhrase = mock(SearchPhrase.class);
        SearchCriteria criteria = mock(SearchCriteria.class);

        when(searchPhrase.isBlank()).thenReturn(phrase == null || phrase.trim().isBlank());
        when(searchPhrase.value()).thenReturn(phrase);
        when(query.phrase()).thenReturn(searchPhrase);

        when(criteria.category()).thenReturn(category);
        when(criteria.location()).thenReturn(location);

        if (mockDateRange) {
            DateRange dateRange = mock(DateRange.class);
            when(dateRange.contains(any(Instant.class))).thenReturn(true);
            when(criteria.dateRange()).thenReturn(dateRange);
        } else {
            when(criteria.dateRange()).thenReturn(null);
        }

        when(query.criteria()).thenReturn(criteria);
        return query;
    }
}
