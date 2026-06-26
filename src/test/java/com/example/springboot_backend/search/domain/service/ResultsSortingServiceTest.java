package com.example.springboot_backend.search.domain.service;

import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.valueobject.SortDirection;
import com.example.springboot_backend.search.domain.valueobject.SortField;
import com.example.springboot_backend.search.domain.valueobject.Sorting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultsSortingServiceTest {

    private final ResultsSortingService service = new ResultsSortingService();

    private SearchEventView eventA;
    private SearchEventView eventB;
    private SearchEventView eventC;
    private List<SearchEventView> unsortedList;

    @BeforeEach
    void setUp() {
        // Przygotowanie danych testowych o zróżnicowanych polach do sortowania
        eventA = new SearchEventView(
                UUID.randomUUID(), "Cinema Open Air", "Description",
                Instant.parse("2026-08-20T18:00:00Z"), "Wrocław", "Rynek", "TEATR", "PUBLISHED"
        );
        eventB = new SearchEventView(
                UUID.randomUUID(), "Acoustic Concert", "Description",
                Instant.parse("2026-08-10T20:00:00Z"), "Wrocław", "Klub", "MUZYKA", "PUBLISHED"
        );
        eventC = new SearchEventView(
                UUID.randomUUID(), "basketball Match", "Description",
                Instant.parse("2026-08-15T12:00:00Z"), "Wrocław", "Hala", "sport", "PUBLISHED"
        );

        unsortedList = List.of(eventA, eventB, eventC);
    }

    @Test
    void sort() {
        // =========================================================================
        // 1. Sortowanie po NAZWIE (NAME) - Rosnąco (ASC)
        // Sprawdza również ignorowanie wielkości liter (Acoustic -> basketball -> Cinema)
        // =========================================================================
        Sorting sortingNameAsc = mock(Sorting.class);
        when(sortingNameAsc.field()).thenReturn(SortField.NAME);
        when(sortingNameAsc.direction()).thenReturn(SortDirection.ASC);

        List<SearchEventView> resultNameAsc = service.sort(unsortedList, sortingNameAsc);

        assertThat(resultNameAsc).containsExactly(eventB, eventC, eventA);

        // =========================================================================
        // 2. Sortowanie po NAZWIE (NAME) - Malejąco (DESC)
        // =========================================================================
        Sorting sortingNameDesc = mock(Sorting.class);
        when(sortingNameDesc.field()).thenReturn(SortField.NAME);
        when(sortingNameDesc.direction()).thenReturn(SortDirection.DESC);

        List<SearchEventView> resultNameDesc = service.sort(unsortedList, sortingNameDesc);

        assertThat(resultNameDesc).containsExactly(eventA, eventC, eventB);

        // =========================================================================
        // 3. Sortowanie po KATEGORII (CATEGORY) - Rosnąco (ASC)
        // Sprawdza ignorowanie wielkości liter (MUZYKA -> sport -> TEATR)
        // =========================================================================
        Sorting sortingCategoryAsc = mock(Sorting.class);
        when(sortingCategoryAsc.field()).thenReturn(SortField.CATEGORY);
        when(sortingCategoryAsc.direction()).thenReturn(SortDirection.ASC);

        List<SearchEventView> resultCategoryAsc = service.sort(unsortedList, sortingCategoryAsc);

        assertThat(resultCategoryAsc).containsExactly(eventB, eventC, eventA);

        // =========================================================================
        // 4. Sortowanie po DACIE (DATE) - Rosnąco (ASC)
        // (10 sierpnia -> 15 sierpnia -> 20 sierpnia)
        // =========================================================================
        Sorting sortingDateAsc = mock(Sorting.class);
        when(sortingDateAsc.field()).thenReturn(SortField.DATE);
        when(sortingDateAsc.direction()).thenReturn(SortDirection.ASC);

        List<SearchEventView> resultDateAsc = service.sort(unsortedList, sortingDateAsc);

        assertThat(resultDateAsc).containsExactly(eventB, eventC, eventA);

        // =========================================================================
        // 5. Sortowanie po DACIE (DATE) - Malejąco (DESC)
        // (20 sierpnia -> 15 sierpnia -> 10 sierpnia)
        // =========================================================================
        Sorting sortingDateDesc = mock(Sorting.class);
        when(sortingDateDesc.field()).thenReturn(SortField.DATE);
        when(sortingDateDesc.direction()).thenReturn(SortDirection.DESC);

        List<SearchEventView> resultDateDesc = service.sort(unsortedList, sortingDateDesc);

        assertThat(resultDateDesc).containsExactly(eventA, eventC, eventB);
    }
}
