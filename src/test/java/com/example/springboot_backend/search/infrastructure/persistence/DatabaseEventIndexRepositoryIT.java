package com.example.springboot_backend.search.infrastructure.persistence;

import com.example.springboot_backend.search.domain.model.SearchEventView;
import com.example.springboot_backend.search.domain.service.EventMatchingService;
import com.example.springboot_backend.search.domain.service.ResultsSortingService;
import com.example.springboot_backend.search.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(DatabaseEventIndexRepository.class)
class DatabaseEventIndexRepositoryIT {

    @MockitoBean
    private SpringDataJpaSearchEventIndexRepository springDataRepository;

    @MockitoBean
    private EventMatchingService matchingService;

    @MockitoBean
    private ResultsSortingService sortingService;

    private DatabaseEventIndexRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DatabaseEventIndexRepository(springDataRepository, matchingService, sortingService);
    }

    @Test
    void search() {
        // given
        SearchQuery query = new SearchQuery(new SearchPhrase("test"), null, null, null);
        JpaSearchEventViewEntity entity = createEntity(UUID.randomUUID(), "Test Event");
        when(springDataRepository.findAll()).thenReturn(List.of(entity));
        when(matchingService.matches(any(), any())).thenReturn(true);
        when(sortingService.sort(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<SearchEventView> results = repository.search(query);

        // then
        assertFalse(results.isEmpty());
        assertEquals("Test Event", results.get(0).title());
        verify(springDataRepository).findAll();
        verify(matchingService).matches(any(), eq(query));
    }

    @Test
    void saveView() {
        // given
        SearchEventView view = createView(UUID.randomUUID(), "New Event");
        JpaSearchEventViewEntity entity = createEntity(view.eventId(), view.title());
        when(springDataRepository.save(any())).thenReturn(entity);

        // when
        SearchEventView saved = repository.saveView(view);

        // then
        assertNotNull(saved);
        assertEquals(view.eventId(), saved.eventId());
        assertEquals("New Event", saved.title());
        verify(springDataRepository).save(any());
    }

    @Test
    void remove() {
        // given
        UUID eventId = UUID.randomUUID();

        // when
        repository.remove(eventId);

        // then
        verify(springDataRepository).deleteById(eventId);
    }

    @Test
    void getCategories() {
        // given
        when(springDataRepository.findDistinctCategories()).thenReturn(List.of("Music", "Sport", "", "  ", "Concert"));

        // when
        List<String> categories = repository.getCategories();

        // then
        assertEquals(3, categories.size());
        assertEquals("Concert", categories.get(0));
        assertEquals("Music", categories.get(1));
        assertEquals("Sport", categories.get(2));
    }

    @Test
    void getLocations() {
        // given
        when(springDataRepository.findDistinctLocations()).thenReturn(List.of("Kraków", "Warszawa", "kraków"));

        // when
        List<String> locations = repository.getLocations();

        // then
        assertEquals(3, locations.size());
        assertTrue(locations.contains("Kraków"));
        assertTrue(locations.contains("Warszawa"));
    }

    @Test
    void clear() {
        // when
        repository.clear();

        // then
        verify(springDataRepository).deleteAll();
    }

    private SearchEventView createView(UUID id, String title) {
        return new SearchEventView(id, title, "Desc", Instant.now(), "City", "Location", "Category", "PUBLISHED");
    }

    private JpaSearchEventViewEntity createEntity(UUID id, String title) {
        return new JpaSearchEventViewEntity(id, title, "Desc", Instant.now(), "City", "Location", "Category", "PUBLISHED");
    }
}